package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import net.minecraft.block.BlockFire;
import net.minecraft.util.IIcon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mitchej123.hodgepodge.textures.AnimationsRenderUtils;

@Mixin(BlockFire.class)
public class MixinBlockFire {

    @ModifyReturnValue(at = @At("RETURN"), method = "getFireIcon")
    private IIcon hodgepodge$markFireAnimationForUpdate(IIcon icon) {
        AnimationsRenderUtils.markBlockTextureForUpdate(icon);
        return icon;
    }
}
