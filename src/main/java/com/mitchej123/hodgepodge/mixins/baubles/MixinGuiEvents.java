package com.mitchej123.hodgepodge.mixins.baubles;

import baubles.client.gui.GuiEvents;
import java.util.Collection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiEvents.class)
public class MixinGuiEvents {

    @Redirect(
            method = "guiPostInit",
            at = @At(value = "INVOKE", target = "Ljava/util/Collection;isEmpty()Z", remap = false),
            remap = false)
    public boolean hodgepodge$fixPotionOffset(@SuppressWarnings("rawtypes") Collection instance) {
        return true;
    }
}
