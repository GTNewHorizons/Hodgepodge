package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.client.gui.GuiVideoSettings;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiVideoSettings.class)
public class MixinGuiVideoSettings {

    /*
     * This mixins is disabled for now as it will crash with Mixins v7.0
     * https://github.com/SpongePowered/Mixin/issues/358
     * TODO enable it once we change to 8.0
     */

    @Dynamic("Method is added by optifine's ASM")
    @Inject(method = "getTooltipLines", at = @At("HEAD"), remap = false, cancellable = true)
    public void hodgepodge$ChangeChunkLoadingTooltip(String btnName, CallbackInfoReturnable<String[]> cir) {
        if (btnName.equals("Chunk Loading")) {
            cir.setReturnValue(new String[] {
                "Chunk Loading is forced to default in GTNH",
                " Any other value can cause your game to crash",
                " If you want to re-enable this setting you",
                " have to open the Hodgepodge config and change",
                "\"fixOptifineChunkLoadingCrash\" to false"
            });
            cir.cancel();
        }
    }
}
