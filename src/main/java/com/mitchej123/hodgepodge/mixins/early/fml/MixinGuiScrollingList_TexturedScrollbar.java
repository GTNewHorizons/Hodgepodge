package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.client.gui.ScrollbarRenderer;

import cpw.mods.fml.client.GuiScrollingList;

/**
 * Replaces GuiScrollingList's (used by GuiModList and other FML screens) flat-colored scrollbar with a textured one.
 * GuiScrollingList has no dedicated "scroll range" helper like GuiSlot's func_148135_f(), so this redirects the
 * specific getContentHeight() call feeding the {@code if (var19 > 0)} check that guards vanilla's own
 * track/thumb/highlight draw calls, returning a value that makes that check false so vanilla's flat-color quads are
 * never built, while we draw our texture in their exact spot using the real content height.
 */
@Mixin(value = GuiScrollingList.class, remap = false)
public abstract class MixinGuiScrollingList_TexturedScrollbar {

    @Shadow
    @Final
    protected int top;

    @Shadow
    @Final
    protected int bottom;

    @Shadow
    @Final
    protected int left;

    @Shadow
    @Final
    protected int listWidth;

    @Shadow
    private float scrollDistance;

    @Shadow
    protected abstract int getContentHeight();

    @Redirect(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/client/GuiScrollingList;getContentHeight()I",
                    ordinal = 2))
    private int hodgepodge$drawTexturedScrollbarAndSuppressVanilla(GuiScrollingList self) {
        int contentHeight = this.getContentHeight();
        int trackHeight = this.bottom - this.top;
        int scrollRange = contentHeight - (trackHeight - 4);

        ScrollbarRenderer.drawScrollbar(
                this.left + this.listWidth - 6,
                this.top,
                trackHeight,
                contentHeight,
                this.scrollDistance,
                scrollRange);

        return trackHeight - 4;
    }
}
