package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiTextField.class)
public abstract class MixinGuiTextField {

    @Shadow
    private boolean isEnabled;

    @Shadow
    private boolean isFocused;

    @Shadow
    public abstract void writeText(String p_146191_1_);

    @Shadow
    public abstract void setCursorPositionEnd();

    @Shadow
    public abstract void setSelectionPos(int p_146199_1_);

    @Shadow
    public abstract String getSelectedText();

    @Inject(method = "textboxKeyTyped", at = @At(value = "HEAD"), cancellable = true)
    public void provideMacOsKeys(char typedChar, int eventKey, CallbackInfoReturnable<Boolean> cir) {
        if (this.isFocused && GuiScreen.isCtrlKeyDown()) {
            if (eventKey == Keyboard.KEY_V) {
                if (this.isEnabled) {
                    this.writeText(GuiScreen.getClipboardString());
                    cir.setReturnValue(true);
                }
            } else if (eventKey == Keyboard.KEY_C) {
                GuiScreen.setClipboardString(this.getSelectedText());
                cir.setReturnValue(true);
            } else if (eventKey == Keyboard.KEY_A) {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                cir.setReturnValue(true);
            } else if (eventKey == Keyboard.KEY_X) {
                GuiScreen.setClipboardString(this.getSelectedText());
                if (this.isEnabled)
                {
                    this.writeText("");
                }
                cir.setReturnValue(true);
            }
        }
    }
}
