package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IGuiScrollingList;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public class MixinGuiScrollingList implements IGuiScrollingList {

    @Shadow(remap = false)
    @Final
    private Minecraft client;

    @Shadow(remap = false)
    @Final
    protected int listWidth;

    @Shadow(remap = false)
    @Final
    protected int bottom;

    @Shadow(remap = false)
    @Final
    protected int top;

    @Shadow(remap = false)
    @Final
    protected int left;

    @Shadow(remap = false)
    @Final
    private int right;

    @Shadow(remap = false)
    private int selectedIndex;

    @Inject(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "cpw/mods/fml/client/GuiScrollingList.applyScrollLimits()V"),
            remap = false)
    private void hodgepodge$glEnableScissor(CallbackInfo ci) {
        ScaledResolution res = new ScaledResolution(client, client.displayWidth, client.displayHeight);
        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                (int) (left * scaleW),
                (int) (client.displayHeight - (bottom * scaleH)),
                (int) (listWidth * scaleW),
                (int) ((this.bottom - this.top) * scaleH));
    }

    @Inject(method = "drawScreen", at = @At("TAIL"), remap = false)
    private void hodgepodge$glDisableScissor(CallbackInfo ci) {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

    }

    @Override
    public int setSelectedIndex(int index) {
        return selectedIndex = index;
    }

    @Override
    public int getBottom() {
        return bottom;
    }

    @Override
    public int getRight() {
        return right;
    }
}
