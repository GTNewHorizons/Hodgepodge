package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.common.KeyBindingDuck;

@Mixin(Minecraft.class)
public class MixinMinecraft_UpdateKeys {

    /**
     * From Sk1er/Patcher
     */
    @Inject(
            method = "setIngameFocus",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    private void hodgepodge$updateKeysStates(CallbackInfo ci) {
        ((KeyBindingDuck) Minecraft.getMinecraft().gameSettings.keyBindAttack).hodgepodge$updateKeyStates();
    }

}
