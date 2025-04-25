package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.ISimulationDistanceWorld;
import com.mitchej123.hodgepodge.SimulationDistanceHelper;

@Mixin(World.class)
public abstract class MixinWorld_SimulationDistance implements ISimulationDistanceWorld {

    @Unique
    private final SimulationDistanceHelper hodgepodge$simulationDistanceHelper = new SimulationDistanceHelper(
            (World) (Object) this);

    public MixinWorld_SimulationDistance() {
        // For Mixin
    }

    @Unique
    @Override
    public SimulationDistanceHelper hodgepodge$getSimulationDistanceHelper() {
        return hodgepodge$simulationDistanceHelper;
    }

    @Override
    public void hodgepodge$preventChunkSimulation(ChunkCoordIntPair chunk, boolean prevent) {
        hodgepodge$simulationDistanceHelper.preventChunkSimulation(chunk, prevent);
    }

    /**
     * Reset internal cache on start of tick
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void hodgepodge$tick(CallbackInfo ci) {
        hodgepodge$simulationDistanceHelper.tickStart();
    }

    /**
     * Skip entities that are outside of simulation distance
     */
    @WrapOperation(
            method = "updateEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;updateEntity(Lnet/minecraft/entity/Entity;)V"))
    private void hodgepodge$updateEntities(World instance, Entity entity, Operation<Void> original) {
        if (hodgepodge$simulationDistanceHelper.shouldProcessTick((int) entity.posX >> 4, (int) entity.posZ >> 4)) {
            original.call(instance, entity);
        }
    }

    /**
     * Skip tile entities that are outside of simulation distance
     */
    @WrapOperation(
            method = "updateEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;updateEntity()V"))
    private void hodgepodge$updateTileEntities(TileEntity entity, Operation<Void> original) {
        if (hodgepodge$simulationDistanceHelper.shouldProcessTick(entity.xCoord >> 4, entity.zCoord >> 4)) {
            original.call(entity);
        }
    }
}
