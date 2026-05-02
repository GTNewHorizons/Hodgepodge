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

    @Redirect(
            method = "processVanilla250Packet",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;length()I", ordinal = 0))
    private int hodgepodge$validateAnvilNameByVisibleChars(String name) {
        if (name.length() <= ANVIL_VISIBLE_LIMIT) return name.length();
        if (name.length() > ANVIL_RAW_LIMIT) return ANVIL_RAW_LIMIT;
        return hodgepodge$countVisibleChars(name);
    }

    @Unique
    private static int hodgepodge$countVisibleChars(String text) {
        int count = 0;
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char ch = text.charAt(i);
            if (ch == '&' && i + 1 < len) {
                char next = Character.toLowerCase(text.charAt(i + 1));
                if (next == 'g' && ColorFormatUtils.isAmpGradientAt(text, i)) {
                    i += ColorFormatUtils.AMP_GRADIENT_LEN - 1;
                    continue;
                }
                if (next == '#' && ColorFormatUtils.isAmpHexAt(text, i)) {
                    i += ColorFormatUtils.AMP_HEX_LEN - 1;
                    continue;
                }
                if (ColorFormatUtils.VALID_AMP_SINGLE_CODES.indexOf(next) != -1) {
                    i++;
                    continue;
                }
            }
            if (ch == ColorFormatUtils.SECTION && i + 1 < len) {
                i++;
                continue;
            }
            count++;
        }
        return count;
    }
}
