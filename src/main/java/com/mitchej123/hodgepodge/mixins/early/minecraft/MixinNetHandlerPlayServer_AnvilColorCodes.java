package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.ColorFormatUtils;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_AnvilColorCodes {

    /** Vanilla's visible-chars cap for anvil item names. */
    private static final int ANVIL_VISIBLE_LIMIT = 30;

    /** Raw length cap we allow past vanilla's limit so {@code &} color codes can fit. Abuse guard. */
    private static final int ANVIL_RAW_LIMIT = 256;

    // Note: 'g' excluded — &g only valid as part of &g&#RRGGBB&#RRGGBB (handled separately below)
    private static final String HODGEPODGE$VALID_CODES = "0123456789abcdefklmnorqzv";

    /**
     * Vanilla checks {@code s.length() <= ANVIL_VISIBLE_LIMIT} for anvil item names and silently drops the name if
     * exceeded. With color codes like {@code &#FF0000} or {@code &z}, the raw string is longer than the visible text.
     * Allow longer raw strings for format codes, but enforce:
     * <ul>
     * <li>visible chars &lt;= {@value #ANVIL_VISIBLE_LIMIT} (same as vanilla's intent)</li>
     * <li>raw length &lt;= {@value #ANVIL_RAW_LIMIT} (safety cap against abuse)</li>
     * </ul>
     */
    @Redirect(
            method = "processVanilla250Packet",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 0))
    private int hodgepodge$validateAnvilNameByVisibleChars(String name) {
        if (name.length() <= ANVIL_VISIBLE_LIMIT) return name.length();
        if (name.length() > ANVIL_RAW_LIMIT) return ANVIL_RAW_LIMIT;
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
            // &#RRGGBB
            if (ch == '&' && i + ColorFormatUtils.AMP_HEX_LEN - 1 < len && text.charAt(i + 1) == '#') {
                boolean valid = true;
                for (int j = 2; j < ColorFormatUtils.AMP_HEX_LEN; j++) {
                    if (Character.digit(text.charAt(i + j), 16) == -1) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    i += ColorFormatUtils.AMP_HEX_LEN - 1;
                    continue;
                }
            }
            // &g&#RRGGBB&#RRGGBB gradient
            if (ch == '&' && i + ColorFormatUtils.AMP_GRADIENT_LEN - 1 < len
                    && Character.toLowerCase(text.charAt(i + 1)) == 'g'
                    && text.charAt(i + 2) == '&'
                    && text.charAt(i + 3) == '#'
                    && text.charAt(i + 2 + ColorFormatUtils.AMP_HEX_LEN) == '&'
                    && text.charAt(i + 3 + ColorFormatUtils.AMP_HEX_LEN) == '#') {
                boolean v1 = true, v2 = true;
                for (int j = 4; j < 2 + ColorFormatUtils.AMP_HEX_LEN; j++) {
                    if (Character.digit(text.charAt(i + j), 16) == -1) {
                        v1 = false;
                        break;
                    }
                }
                if (v1) {
                    for (int j = 4 + ColorFormatUtils.AMP_HEX_LEN; j < ColorFormatUtils.AMP_GRADIENT_LEN; j++) {
                        if (Character.digit(text.charAt(i + j), 16) == -1) {
                            v2 = false;
                            break;
                        }
                    }
                }
                if (v1 && v2) {
                    i += ColorFormatUtils.AMP_GRADIENT_LEN - 1;
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
