package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.storage.ISaveHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;

/**
 * Must run after MixinWorldClient_FixAllocations
 */
@Mixin(value = WorldServer.class, priority = 1001)
public abstract class MixinWorldServer_SimulationDistance extends World implements ISimulationDistanceWorld {

    @Shadow
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    @Shadow
    private List<NextTickListEntry> pendingTickListEntriesThisTick;

    @Unique
    private boolean hodgepodge$processCurrentChunk;

    @Unique
    private ExtendedBlockStorage[] hodgepodge$emptyBlockStorage = new ExtendedBlockStorage[0];

    public MixinWorldServer_SimulationDistance() {
        super(null, null, (WorldProvider) null, null, null);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void hodgepodge$initSimulationHelper(MinecraftServer p_i45284_1_, ISaveHandler p_i45284_2_,
            String p_i45284_3_, int p_i45284_4_, WorldSettings p_i45284_5_, Profiler p_i45284_6_, CallbackInfo ci) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        helper.setServerVariables(pendingTickListEntriesTreeSet, pendingTickListEntriesHashSet, this::chunkExists);
    }

    /**
     * Fake HashSet size so the original MC code doesn't process any ticks
     */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;size()I"))
    private int hodgepodge$fakeTreeSetSize(TreeSet<NextTickListEntry> instance) {
        return 0;
    }

    /**
     * Fake TreeSet size to match HashSet size to prevent IllegalStateException
     */
    @Redirect(method = "tickUpdates", at = @At(value = "INVOKE", target = "Ljava/util/Set;size()I"))
    private int hodgepodge$fakeHashSetSize(Set<NextTickListEntry> instance) {
        return 0;
    }

    /**
     * Skip ticks that are outside of simulation distance, and delete ticks for unloaded chunks
     */
    @Inject(
            method = "tickUpdates",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;startSection(Ljava/lang/String;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER))
    private void hodgepodge$tickUpdates(boolean p_72955_1_, CallbackInfoReturnable<Boolean> cir) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        helper.tickUpdates(p_72955_1_, pendingTickListEntriesThisTick);
    }

    /**
     * Cache whether the current chunk is outside of simulation distance or not
     */
    @WrapOperation(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object hodgepodge$chunkTicks(Iterator<ChunkCoordIntPair> instance, Operation<ChunkCoordIntPair> original) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        ChunkCoordIntPair result = original.call(instance);
        hodgepodge$processCurrentChunk = helper.shouldProcessTick(result.chunkXPos, result.chunkZPos);
        return result;
    }

    /**
     * Skip lighting outside of simulation distance
     */
    @WrapOperation(
            method = "func_147456_g",
            at = @At(
                    value = "INVOKE",
                    remap = false,
                    target = "Lnet/minecraft/world/WorldProvider;canDoLightning(Lnet/minecraft/world/chunk/Chunk;)Z"))
    private boolean hodgepodge$canDoLighting(WorldProvider instance, Chunk chunk, Operation<Boolean> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance, chunk);
        }

        return false;
    }

    /**
     * Skip rain/icde logic outside of simulation distance
     */
    @WrapOperation(
            method = "func_147456_g",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    remap = false,
                    target = "Lnet/minecraft/world/WorldProvider;canDoRainSnowIce(Lnet/minecraft/world/chunk/Chunk;)Z"))
    private boolean hodgepodge$canDoRainSnowIce(WorldProvider instance, Chunk chunk, Operation<Boolean> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance, chunk);
        }

        return false;
    }

    /**
     * Skip random ticks outside of simulation distance
     */
    @WrapOperation(
            method = "func_147456_g",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/chunk/Chunk;getBlockStorageArray()[Lnet/minecraft/world/chunk/storage/ExtendedBlockStorage;"))
    private ExtendedBlockStorage[] hodgepodge$randomTicks(Chunk instance, Operation<ExtendedBlockStorage[]> original) {
        if (hodgepodge$processCurrentChunk) {
            return original.call(instance);
        }

        return hodgepodge$emptyBlockStorage;
    }

    /**
     * Handle adding ticks
     */
    @WrapOperation(
            method = "scheduleBlockUpdateWithPriority",
            at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$addTick1(TreeSet<NextTickListEntry> instance, Object e, Operation<Boolean> original) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        helper.addTick((NextTickListEntry) e);
        return original.call(instance, e);
    }

    /**
     * Handle adding ticks
     */
    @WrapOperation(
            method = "func_147446_b",
            at = @At(value = "INVOKE", target = "Ljava/util/TreeSet;add(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$addTick2(TreeSet<NextTickListEntry> instance, Object e, Operation<Boolean> original) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        helper.addTick((NextTickListEntry) e);
        return original.call(instance, e);
    }

}
