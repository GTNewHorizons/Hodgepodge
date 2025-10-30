package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Set;
import java.util.TreeSet;

import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer_SimulationDistanceThermosFix extends World implements ISimulationDistanceWorld {

    @Shadow
    private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;

    @Shadow
    private Set<NextTickListEntry> pendingTickListEntriesHashSet;

    public MixinWorldServer_SimulationDistanceThermosFix() {
        super(null, null, (WorldProvider) null, null, null);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;ILnet/minecraft/world/WorldSettings;Lnet/minecraft/profiler/Profiler;Lorg/bukkit/World$Environment;Lorg/bukkit/generator/ChunkGenerator;)V",
            at = @At("TAIL"),
            require = 1)
    private void hodgepodge$initSimulationHelperThermos(CallbackInfo ci) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        helper.setServerVariables(pendingTickListEntriesTreeSet, pendingTickListEntriesHashSet, this::chunkExists);
    }

    /**
     * Cache whether the current chunk is outside of simulation distance or not
     */

    @WrapOperation(
            method = "func_147456_g",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;chunkExists(II)Z"),
            require = 1)
    private boolean hodgepodge$chunkTicks(WorldServer instance, int x, int y, Operation<Boolean> original) {
        SimulationDistanceHelper helper = hodgepodge$getSimulationDistanceHelper();
        hodgepodge$SetProcessCurrentChunk(helper.shouldProcessTick(x, y));
        return original.call(instance, x, y);
    }
}
