package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.multiplayer.PlayerControllerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP_BlockHitDelay {

    @Shadow
    private int blockHitDelay;

    @Inject(method = "clickBlock", at = @At("HEAD"))
    public void clickBlock(int x, int y, int z, int side, CallbackInfo ci) {
        blockHitDelay = 0;
    }
}
