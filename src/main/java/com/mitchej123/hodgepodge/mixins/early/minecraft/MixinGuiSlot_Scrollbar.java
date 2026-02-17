package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.mitchej123.hodgepodge.client.bettermodlist.ScrollbarColorHelper;

@Mixin(GuiSlot.class)
public class MixinGuiSlot_Scrollbar {

    @Shadow
    @Final
    private Minecraft mc;

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 4))
    private void hodgepodge$recolorScrollbarTrack(Args args) {
        args.set(0, ScrollbarColorHelper.getTrackColor(mc));
    }

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 5))
    private void hodgepodge$recolorScrollbarThumb(Args args) {
        args.set(0, ScrollbarColorHelper.getThumbColor(mc));
    }

    @ModifyArgs(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_I(II)V",
                    ordinal = 6))
    private void hodgepodge$recolorScrollbarThumbHighlight(Args args) {
        args.set(0, ScrollbarColorHelper.getThumbHighlightColor(mc));
    }
}
