package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(Minecraft.class)
public class MixinMinecraft_KeyInputEventOnMouseButton {

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;fireMouseInput()V",
                    shift = At.Shift.AFTER))
    public void hodgepodge$fireMouseButtonPressAsKeyInputEvent(CallbackInfo ci) {
        if (Mouse.getEventButton() != -1) {
            FMLCommonHandler.instance().fireKeyInput();
        }
    }

}
