package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_SignLimit {

    /**
     * Vanilla checks {@code length() > 15} and replaces the line with "!?" if exceeded. We allow longer raw strings for
     * format codes, but enforce: - visible chars <= 15 (same as vanilla's intent) - raw length <= 90 (safety cap
     * against abuse)
     */
    @Redirect(
            method = "processUpdateSign",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 0))
    private int hodgepodge$validateSignLineLength(String str) {
        if (str.length() <= 15) return str.length();
        if (str.length() > 90) return 90;
        return FontRendering.countVisibleChars(str);
    }
}
