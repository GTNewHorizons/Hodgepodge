package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiIngame;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.client.title.TitleAPI;

@Mixin(GuiIngame.class)
public class MixinGuiIngame_TitleTick {

    @Inject(method = "updateTick", at = @At("TAIL"))
    private void hodgepodge$tickTitle(CallbackInfo ci) {
        TitleAPI.tick();
    }
}
