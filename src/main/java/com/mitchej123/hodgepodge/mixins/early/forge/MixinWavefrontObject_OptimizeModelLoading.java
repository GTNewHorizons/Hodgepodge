package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraftforge.client.model.obj.WavefrontObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.libraries.com.google.common.base.CharMatcher;

import java.util.regex.Pattern;

@Mixin(value = WavefrontObject.class, remap = false)
public class MixinWavefrontObject_OptimizeModelLoading {

    @Shadow private static Pattern vertexPattern;
    @Shadow private static Pattern vertexNormalPattern;
    @Shadow private static Pattern textureCoordinatePattern;
    @Shadow private static Pattern face_V_VT_VN_Pattern;
    @Shadow private static Pattern face_V_VT_Pattern;
    @Shadow private static Pattern face_V_VN_Pattern;
    @Shadow private static Pattern face_V_Pattern;
    @Shadow private static Pattern groupObjectPattern;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void optimizePatterns(CallbackInfo ci) {
        vertexPattern = Pattern.compile("v(?: -?\\d++(?:\\.\\d++)?){3,4}");
        vertexNormalPattern = Pattern.compile("vn(?: -?\\d++(?:\\.\\d++)?){3,4}");
        textureCoordinatePattern = Pattern.compile("vt(?: -?\\d++(?:\\.\\d++)?){2,3}");
        face_V_VT_VN_Pattern = Pattern.compile("f(?: \\d++/\\d++/\\d++){3,4}");
        face_V_VT_Pattern = Pattern.compile("f(?: \\d++/\\d++){3,4}");
        face_V_VN_Pattern = Pattern.compile("f(?: \\d++//\\d++){3,4}");
        face_V_Pattern = Pattern.compile("f(?: \\d++){3,4}");
        groupObjectPattern = Pattern.compile("[go] [\\w.]++");
    }

    @Redirect(
            method = "loadObjModel(Ljava/io/InputStream;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;replaceAll(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
            )
    )
    private String optimizedWhitespaceReplacement(String original, String regex, String replacement) {
        return CharMatcher.whitespace().trimAndCollapseFrom(original, ' ');
    }
}
