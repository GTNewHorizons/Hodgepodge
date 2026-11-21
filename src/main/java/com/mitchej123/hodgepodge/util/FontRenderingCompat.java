package com.mitchej123.hodgepodge.util;

import net.minecraft.client.gui.GuiTextField;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;
import com.gtnewhorizon.gtnhlib.util.font.TextFieldFormatting;

import cpw.mods.fml.common.Loader;

/** Checks for GTNHLib font rendering methods that may not exist in older versions. */
public class FontRenderingCompat {

    public static final boolean HAS_PREPROCESS_TEXT;
    public static final boolean HAS_AMP_CODE_AWARE_API;

    static {
        boolean found = false;
        try {
            FontRendering.class.getMethod("preprocessText", String.class);
            found = true;
        } catch (NoSuchMethodException ignored) {}
        HAS_PREPROCESS_TEXT = found;

        boolean api = false;
        try {
            FontRendering.class.getMethod("preprocessorHandlesAmpCodes");
            Class.forName("com.gtnewhorizon.gtnhlib.util.font.TextFieldFormatting");
            api = true;
        } catch (ReflectiveOperationException ignored) {}

        HAS_AMP_CODE_AWARE_API = api;
    }

    /** Only call when {@link #HAS_PREPROCESS_TEXT} is true. */
    public static String preprocessText(String s) {
        return FontRendering.preprocessText(s);
    }

    /** Register the legacy fallback preprocessor if Angelica is not installed. */
    public static void registerFallbackIfNoAngelica() {
        if (!HAS_PREPROCESS_TEXT) return;
        if (Loader.isModLoaded("angelica")) return;
        FontRendering.setTextPreprocessor(new LegacyColorFallback());
    }

    public static boolean editsAmpCodesAtomically(GuiTextField field) {
        if (!HAS_AMP_CODE_AWARE_API) return HAS_PREPROCESS_TEXT;
        return FontRendering.preprocessorHandlesAmpCodes() && TextFieldFormatting.isAmpCodeAware(field);
    }
}
