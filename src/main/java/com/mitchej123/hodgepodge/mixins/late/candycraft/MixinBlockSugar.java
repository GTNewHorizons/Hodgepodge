package com.mitchej123.hodgepodge.mixins.late.candycraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.valentin4311.candycraftmod.BlockSugar;

@Mixin(BlockSugar.class)
public class MixinBlockSugar {

    @Inject(at = @At("HEAD"), cancellable = true, method = "onBlockActivated")
    private void hodgepodge$fixNPE(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer,
            int par6, float par7, float par8, float par9, CallbackInfoReturnable<Boolean> cir) {
        if (par5EntityPlayer.getCurrentEquippedItem() == null) {
            cir.setReturnValue(false);
        }
    }

}
