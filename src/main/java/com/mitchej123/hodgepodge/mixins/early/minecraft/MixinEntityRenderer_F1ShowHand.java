package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.client.F1State;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_F1ShowHand {

    @ModifyExpressionValue(
            method = "renderHand",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/settings/GameSettings;hideGUI:Z"))
    private boolean hodgepodge$shouldHideHand(boolean original) {
        return F1State.state == F1State.HIDE_ALL;
    }
}
