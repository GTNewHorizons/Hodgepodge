package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ic2.core.block.BlockRubSapling;

@Mixin(BlockRubSapling.class)
public class MixinBlockRubSapling {

    @Inject(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;swingItem()V",
                    shift = At.Shift.AFTER),
            cancellable = true)
    private void hodgepodge$markBonemealAsHandled(World world, int x, int y, int z, EntityPlayer player, int side,
            float subX, float subY, float subZ, CallbackInfoReturnable<Boolean> cir) {
        // IC2 applies and consumes bone meal itself. Returning false lets ItemDye apply it a second time, which can
        // reduce the last item past zero and leave an infinitely reusable, wrapping stack in the inventory.
        cir.setReturnValue(true);
    }
}
