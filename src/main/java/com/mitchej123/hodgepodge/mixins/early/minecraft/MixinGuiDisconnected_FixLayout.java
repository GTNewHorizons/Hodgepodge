package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vertically centers the disconnect screen as a group so the button doesn't overlap long kick messages.
 *
 * <pre>
 * Layout (top to bottom, group centered on the screen):
 *   Title                 (FONT_HEIGHT = 9px)
 *   20px gap              (padding: 10 below title + 10 above text)
 *   Wrapped reason text   (n * FONT_HEIGHT)
 *   12px gap              (padding: 10 below text + 2 above button)
 *   Button                (20px tall)
 * </pre>
 */
@Mixin(GuiDisconnected.class)
public class MixinGuiDisconnected_FixLayout extends GuiScreen {

    @Shadow
    private String field_146306_a; // title string

    @Shadow
    private List field_146305_g; // wrapped text lines

    @Unique
    private int hodgepodge$titleY;

    @Unique
    private int hodgepodge$textStartY;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void hodgepodge$centerLayout(CallbackInfo ci) {
        int lineHeight = this.fontRendererObj.FONT_HEIGHT;
        int textLines = field_146305_g != null ? field_146305_g.size() : 0;

        // Inter-element gap = bottom padding of previous + top padding of next.
        // title→text = 10+10 = 20, text→button = 10+2 = 12.
        int totalHeight = lineHeight + 20 + (textLines * lineHeight) + 12 + 20;

        int topY = Math.max(5, (this.height - totalHeight) / 2);

        hodgepodge$titleY = topY;
        hodgepodge$textStartY = topY + lineHeight + 20;
        int buttonY = hodgepodge$textStartY + (textLines * lineHeight) + 12;

        // Clamp so button stays on screen (button is 20px tall)
        if (buttonY + 20 > this.height - 5) {
            buttonY = this.height - 25;
        }

        for (Object obj : this.buttonList) {
            GuiButton button = (GuiButton) obj;
            if (button.id == 0) {
                button.yPosition = buttonY;
                break;
            }
        }
    }

    /**
     * Vanilla hardcodes title at height/2-50 and text at height/2-30. We cancel and redraw at the centered positions,
     * then let super render the buttons from buttonList.
     */
    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ci.cancel();

        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, this.field_146306_a, this.width / 2, hodgepodge$titleY, 0xAAAAAA);

        if (this.field_146305_g != null) {
            int y = hodgepodge$textStartY;
            for (Iterator<?> iterator = this.field_146305_g.iterator(); iterator
                    .hasNext(); y += this.fontRendererObj.FONT_HEIGHT) {
                String s = (String) iterator.next();
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, y, 0xFFFFFF);
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
