package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.client.gui.ScrollbarRenderer;

/**
 * Replaces vanilla's flat-colored scrollbar with a textured one. Redirects the second call to func_148135_f()
 * (GuiSlot's "how much scrollable range is left" helper) in drawScreen -- the one feeding the {@code if (i3 > 0)} check
 * that guards vanilla's own track/thumb/highlight draw calls. We use the real value to draw our texture in vanilla's
 * exact spot, then return 0 so vanilla's own flat-color quads are never even built, avoiding a double draw entirely.
 */
@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot_TexturedScrollbar {

    @Shadow
    public int top;

    @Shadow
    public int bottom;

    @Shadow
    private float amountScrolled;

    @Shadow
    protected abstract int getScrollBarX();

    @Shadow
    protected abstract int getContentHeight();

    @Shadow
    public abstract int func_148135_f();

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiSlot;func_148135_f()I", ordinal = 1))
    private int hodgepodge$drawTexturedScrollbarAndSuppressVanilla(GuiSlot self) {
        int scrollRange = this.func_148135_f();
        int trackHeight = this.bottom - this.top;
        ScrollbarRenderer.drawScrollbar(
                this.getScrollBarX(),
                this.top,
                trackHeight,
                this.getContentHeight(),
                this.amountScrolled,
                scrollRange);

        return 0;
    }
}
