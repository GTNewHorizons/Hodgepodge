package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_AnvilColorCodes {

    private static final String HODGEPODGE$VALID_CODES = "0123456789abcdefklmnorywjg";

    /**
     * Vanilla checks {@code s.length() <= 30} for anvil item names and silently drops the name if exceeded. With color
     * codes like {@code &#FF0000} or {@code &w}, the raw string is longer than the visible text. Allow longer raw
     * strings for format codes, but enforce:
     * <ul>
     * <li>visible chars &lt;= 30 (same as vanilla's intent)</li>
     * <li>raw length &lt;= 256 (safety cap against abuse)</li>
     * </ul>
     */
    @Redirect(
            method = "processVanilla250Packet",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 0))
    private int hodgepodge$validateAnvilNameByVisibleChars(String name) {
        if (name.length() <= 30) return name.length();
        if (name.length() > 256) return 256;
        return hodgepodge$countVisibleChars(name);
    }

    /**
     * Count visible characters in raw text, skipping valid {@code &} format codes and {@code §} format pairs. Does not
     * depend on a registered preprocessor.
     */
    @Unique
    private static int hodgepodge$countVisibleChars(String text) {
        int count = 0;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);
            // &#RRGGBB (8 chars)
            if (ch == '&' && i + 1 < len && text.charAt(i + 1) == '#' && i + 7 < len) {
                boolean valid = true;
                for (int j = 2; j <= 7; j++) {
                    if (Character.digit(text.charAt(i + j), 16) == -1) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    i += 7;
                    continue;
                }
            }
            // &X single format code (2 chars)
            if (ch == '&' && i + 1 < len) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                if (HODGEPODGE$VALID_CODES.indexOf(code) != -1) {
                    i++;
                    continue;
                }
            }
            // §X format pair (2 chars)
            if (ch == '\u00a7' && i + 1 < len) {
                i++;
                continue;
            }
            count++;
        }
        return count;
    }
}
