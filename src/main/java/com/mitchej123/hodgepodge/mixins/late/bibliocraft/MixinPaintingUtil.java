package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import java.net.URI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import jds.bibliocraft.PaintingUtil;

@Mixin(value = PaintingUtil.class)
public class MixinPaintingUtil {

    @Redirect(
            method = "getJarPaintings",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/net/URLDecoder;decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"),
            remap = false)
    private static String hodgepodge$fixJarPath(String path, String encoding) throws Exception {
        return URI.create("file://" + path).getPath();
    }
}
