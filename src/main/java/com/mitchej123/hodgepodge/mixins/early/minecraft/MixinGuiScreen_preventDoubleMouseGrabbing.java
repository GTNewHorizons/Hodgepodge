package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiScreen.class)
public class MixinGuiScreen_preventDoubleMouseGrabbing {

    /**
     * @author danyadev
     * @reason prevent moving cursor to the center of the window when pressing Esc to leave a GUI screen: world list,
     *         server list, options, etc...
     */
    @Redirect(
            method = "keyTyped(CI)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setIngameFocus()V"))
    private void hodgepodge$setIngameFocusNoop(Minecraft mc) {
        // Noop. The 'this.mc.displayGuiScreen((GuiScreen)null)' line above this method already have setIngameFocus()
    }
}
