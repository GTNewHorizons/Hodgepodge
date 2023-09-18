package com.mitchej123.hodgepodge.mixin.mixins.early.minecraft.textures.client;

import net.minecraft.block.BlockFire;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.textures.AnimationsRenderUtils;

@Mixin(BlockFire.class)
public class MixinBlockFire {

    @Shadow
    private IIcon[] field_149850_M;

    @Inject(method = "getFireIcon", at = @At("HEAD"))
    private void hodgepodge$markFireAnimationForUpdate(int p_149840_1_, CallbackInfoReturnable<IIcon> cir) {
        AnimationsRenderUtils.markBlockTextureForUpdate(field_149850_M[p_149840_1_]);
    }
}
