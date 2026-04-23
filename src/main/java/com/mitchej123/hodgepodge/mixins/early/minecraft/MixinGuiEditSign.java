package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.inventory.GuiEditSign;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.mitchej123.hodgepodge.util.SignLimits;

@Mixin(GuiEditSign.class)
public class MixinGuiEditSign {

    @Redirect(method = "keyTyped", at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 2))
    private int hodgepodge$signVisibleLength(String str) {
        if (str.length() >= SignLimits.RAW) return SignLimits.VISIBLE;
        return FontRendering.countVisibleChars(str);
    }
}
