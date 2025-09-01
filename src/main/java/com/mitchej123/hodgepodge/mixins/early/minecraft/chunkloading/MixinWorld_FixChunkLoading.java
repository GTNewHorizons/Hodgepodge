package com.mitchej123.hodgepodge.mixins.early.minecraft.chunkloading;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld_FixChunkLoading {

    @Shadow
    public boolean isRemote;

    @Shadow
    public boolean blockExists(int x, int y, int z) {
        throw new RuntimeException("Mixin failed to apply");
    }

    @Inject(method = "notifyBlockOfNeighborChange", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$onNotifyBlockOfNeighborChange(int x, int y, int z, final Block block, CallbackInfo ci) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            ci.cancel();
        }
    }

    @Inject(method = "getBlockLightValue_do", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$onGetBlockLightValue_do(int x, int y, int z, boolean checkNeighbors,
            CallbackInfoReturnable<Integer> ci) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            ci.setReturnValue(0);
        }
    }

    @Inject(method = "getIndirectPowerLevelTo", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$onGetIndirectPowerLevelTo(int x, int y, int z, int directionIn,
            CallbackInfoReturnable<Integer> ci) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            ci.setReturnValue(0);
        }
    }

    @Inject(method = "isBlockProvidingPowerTo", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$onIsBlockProvidingPowerTo(int x, int y, int z, int directionIn,
            CallbackInfoReturnable<Integer> ci) {
        if (!this.isRemote && !blockExists(x, y, z)) {
            ci.setReturnValue(0);
        }
    }

    @Redirect(
            method = "func_147453_f",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;"))
    private Block hodgepodge$notifyNeighborChange(World world, int x, int y, int z) {
        if (!this.isRemote && !this.blockExists(x, y, z)) {
            return Blocks.air;
        }
        return world.getBlock(x, y, z);
    }

}
