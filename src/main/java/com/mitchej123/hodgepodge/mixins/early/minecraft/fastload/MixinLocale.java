package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.resources.Locale;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.base.Splitter;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.util.OffThreadLineIterator;

@Mixin(Locale.class)
public class MixinLocale {

    @Redirect(
            method = "loadLocaleData(Ljava/io/InputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/commons/io/IOUtils;readLines(Ljava/io/InputStream;Ljava/nio/charset/Charset;)Ljava/util/List;",
                    remap = false))
    private List<String> readNoLines(InputStream input, Charset encoding) {
        return Collections.emptyList();
    }

    @Redirect(
            method = "loadLocaleData(Ljava/io/InputStream;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator<String> createOffThreadIterator(List<String> instance, @Local(argsOnly = true) InputStream input) {
        return new OffThreadLineIterator(input);
    }

    @Redirect(
            method = "loadLocaleData(Ljava/io/InputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/base/Splitter;split(Ljava/lang/CharSequence;)Ljava/lang/Iterable;",
                    remap = false))
    private Iterable<String> dontSplit(Splitter instance, CharSequence sequence) {
        return null;
    }

    @Redirect(
            method = "loadLocaleData(Ljava/io/InputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Iterables;toArray(Ljava/lang/Iterable;Ljava/lang/Class;)[Ljava/lang/Object;",
                    remap = false))
    private Object[] split(Iterable<String> iterable, Class<String> type, @Local String s) {
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '=') {
                return new String[] { s.substring(0, i), s.substring(i + 1) };
            }
        }
        return null;
    }

}
