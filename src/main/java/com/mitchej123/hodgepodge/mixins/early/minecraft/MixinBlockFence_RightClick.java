package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockFence.class)
public class MixinBlockFence_RightClick {

    /**
     * @author JB
     * @reason Fix fence always right-clicking
     */
    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX,
            float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn.isRemote) {
            cir.setReturnValue(false);
        }
    }
}
