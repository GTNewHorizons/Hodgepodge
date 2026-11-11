package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.mitchej123.hodgepodge.util.SignLimits;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_SignLimit {

    /**
     * Vanilla checks {@code length() > SignLimits.VISIBLE} and replaces the line with "!?" if exceeded. We allow longer
     * raw strings for format codes, but enforce: visible chars &lt;= {@link SignLimits#VISIBLE} (vanilla's intent), raw
     * length &lt;= {@link SignLimits#RAW} (safety cap against abuse).
     */
    @Redirect(
            method = "processUpdateSign",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 0))
    private int hodgepodge$validateSignLineLength(String str) {
        if (str.length() <= SignLimits.VISIBLE) return str.length();
        if (str.length() > SignLimits.RAW) return SignLimits.RAW;
        return FontRendering.countVisibleChars(str);
    }
}
