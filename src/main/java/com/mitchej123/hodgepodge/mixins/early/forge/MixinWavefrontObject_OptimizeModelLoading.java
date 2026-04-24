package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.client.model.obj.WavefrontObject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WavefrontObject.class, remap = false)
public class MixinWavefrontObject_OptimizeModelLoading {

    @Redirect(
            method = "loadObjModel(Ljava/io/InputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
    private String optimizedCollapseWhitespace(String string, String regex, String replacement) {
        final int length = string.length();
        final StringBuilder normalized = new StringBuilder(length);
        boolean pendingSpace = false;
        boolean changed = false;

        for (int i = 0; i < length; i++) {
            final char c = string.charAt(i);

            if (c == ' ' || c == '\t' || c == '\r') {
                changed |= pendingSpace || c != ' ';
                pendingSpace = true;
                continue;
            }

            if (pendingSpace) {
                normalized.append(' ');
                pendingSpace = false;
            }

            normalized.append(c);
        }

        return changed ? normalized.toString() : string;
    }

    /**
     * Regex: Pattern.compile("v(?: -?\\d++(?:\\.\\d++)?){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidVertexLine(String str) {
        return hodgepodge$isNumericLine(str, "v ", 3, 4);
    }

    /**
     * Regex: Pattern.compile("vn(?: -?\\d++(?:\\.\\d++)?){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidVertexNormalLine(String str) {
        return hodgepodge$isNumericLine(str, "vn ", 3, 4);
    }

    /**
     * Regex: Pattern.compile("vt(?: -?\\d++(?:\\.\\d++)?){2,3}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidTextureCoordinateLine(String str) {
        return hodgepodge$isNumericLine(str, "vt ", 2, 3);
    }

    /**
     * Regex: Pattern.compile("f(?: \\d++/\\d++/\\d++){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidFace_V_VT_VN_Line(String str) {
        if (!str.startsWith("f ")) return false;

        int length = str.length();
        int groupCount = 0;
        int i = 2;

        while (i < length) {
            int firstSlash = str.indexOf('/', i);
            int secondSlash = str.indexOf('/', firstSlash + 1);
            int space = str.indexOf(' ', i);
            if (space == -1) space = length;

            if (firstSlash == -1 || secondSlash == -1) return false;
            if (firstSlash <= i || secondSlash <= firstSlash + 1 || secondSlash >= space - 1) return false;
            if (!hodgepodge$isDigits(str, i, firstSlash) || !hodgepodge$isDigits(str, firstSlash + 1, secondSlash)
                    || !hodgepodge$isDigits(str, secondSlash + 1, space))
                return false;

            groupCount++;
            if (groupCount > 4) return false;
            i = space + 1;
        }

        return groupCount >= 3;
    }

    /**
     * Regex: Pattern.compile("f(?: \\d++/\\d++){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidFace_V_VT_Line(String str) {
        if (!str.startsWith("f ")) return false;

        int length = str.length();
        int groupCount = 0;
        int i = 2;

        while (i < length) {
            int slash = str.indexOf('/', i);
            int space = str.indexOf(' ', i);
            if (space == -1) space = length;

            if (slash <= i || slash >= space - 1) return false;
            if (!hodgepodge$isDigits(str, i, slash) || !hodgepodge$isDigits(str, slash + 1, space)) return false;

            groupCount++;
            if (groupCount > 4) return false;
            i = space + 1;
        }

        return groupCount >= 3;
    }

    /**
     * Regex: Pattern.compile("f(?: \\d++//\\d++){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidFace_V_VN_Line(String str) {
        if (!str.startsWith("f ")) return false;

        int length = str.length();
        int groupCount = 0;
        int i = 2;

        while (i < length) {
            int slashes = str.indexOf("//", i);
            int space = str.indexOf(' ', i);
            if (space == -1) space = length;

            if (slashes <= i || slashes >= space - 2) return false;
            if (!hodgepodge$isDigits(str, i, slashes) || !hodgepodge$isDigits(str, slashes + 2, space)) return false;

            groupCount++;
            if (groupCount > 4) return false;
            i = space + 1;
        }

        return groupCount >= 3;
    }

    /**
     * Regex: Pattern.compile("f(?: \\d++){3,4}")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidFace_V_Line(String str) {
        if (!str.startsWith("f ")) return false;

        int length = str.length();
        int groupCount = 0;
        int i = 2;

        while (i < length) {
            int space = str.indexOf(' ', i);
            if (space == -1) space = length;

            if (!hodgepodge$isDigits(str, i, space)) return false;

            groupCount++;
            if (groupCount > 4) return false;
            i = space + 1;
        }

        return groupCount >= 3;
    }

    /**
     * Regex: Pattern.compile("[go] [\\w.]++")
     * 
     * @author danyadev
     * @reason superior implementation
     */
    @Overwrite
    private static boolean isValidGroupObjectLine(String str) {
        int length = str.length();

        if (length < 3) return false;
        if (str.charAt(0) != 'g' && str.charAt(0) != 'o') return false;
        if (str.charAt(1) != ' ') return false;

        for (int i = 2; i < length; i++) {
            char c = str.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '.') {
                continue;
            }
            return false;
        }

        return true;
    }

    @Unique
    private static boolean hodgepodge$isDigits(String str, int from, int to) {
        if (from >= to) return false;

        for (int i = from; i < to; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') return false;
        }

        return true;
    }

    /**
     * Returns the end index of the number or -1 if it's not a valid number
     */
    @Unique
    private static int hodgepodge$scanNumber(String str, int from) {
        int length = str.length();

        if (from >= length) return -1;
        if (str.charAt(from) == '-') from++;
        if (from >= length) return -1;

        int intStart = from;
        while (from < length) {
            char c = str.charAt(from);
            if (c < '0' || c > '9') break;
            from++;
        }
        if (from == intStart) return -1;

        if (from < length && str.charAt(from) == '.') {
            from++;
            int fracStart = from;
            while (from < length) {
                char c = str.charAt(from);
                if (c < '0' || c > '9') break;
                from++;
            }
            if (from == fracStart) return -1;
        }

        return from;
    }

    // For example, prefix = "v ", minGroups = 3, maxGroups = 4.
    // This is the correct line: "v 1 -3 1.25"
    @Unique
    private static boolean hodgepodge$isNumericLine(String str, String prefix, int minGroups, int maxGroups) {
        if (!str.startsWith(prefix)) return false;

        int length = str.length();
        int i = prefix.length();
        int groupCount = 0;

        while (i < length) {
            int next = str.indexOf(' ', i);
            if (next == -1) next = length;

            int end = hodgepodge$scanNumber(str, i);
            if (end != next) return false;

            groupCount++;
            if (groupCount > maxGroups) return false;
            i = next + 1;
        }

        return groupCount >= minGroups;
    }
}
