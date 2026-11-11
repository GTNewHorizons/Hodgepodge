package com.mitchej123.hodgepodge.mixins.early.fml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;

import cpw.mods.fml.common.discovery.JarDiscoverer;

@Mixin(value = JarDiscoverer.class, remap = false)
public class MixinJarDiscoverer {

    // We won't use this matcher anymore so no need for a matcher object allocation
    @Redirect(
            method = "discover",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/regex/Pattern;matcher(Ljava/lang/CharSequence;)Ljava/util/regex/Matcher;"),
            require = 1)
    private Matcher hodgepodge$nullMatcher(Pattern pattern, CharSequence input) {
        return null;
    }

    // A faster implementation for "[^\\s\\$]+(\\$[^\\s]+)?\\.class$" matcher
    @Redirect(
            method = "discover",
            at = @At(value = "INVOKE", target = "Ljava/util/regex/Matcher;matches()Z"),
            require = 1)
    private boolean hodgepodge$fastFilenameValidator(Matcher matcher, @Local(name = "ze") ZipEntry zipEntry) {
        String name = zipEntry.getName();
        if (!name.endsWith(".class")) return false;

        final int end = name.length() - ".class".length();
        if (end == 0) return false;

        boolean seenDollar = false;

        for (int i = 0; i < end; i++) {
            final char c = name.charAt(i);
            if (Character.isWhitespace(c)) return false;

            if (!seenDollar && c == '$') {
                if (i == 0 || i == end - 1) return false;
                seenDollar = true;
            }
        }

        return true;
    }
}
