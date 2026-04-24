package com.mitchej123.hodgepodge.util;

import com.gtnewhorizon.gtnhlib.util.font.FontRendering;

/** Checks for GTNHLib font rendering methods that may not exist in older versions. */
public class FontRenderingCompat {

    public static final boolean HAS_PREPROCESS_TEXT;

    static {
        boolean found = false;
        try {
            FontRendering.class.getMethod("preprocessText", String.class);
            found = true;
        } catch (NoSuchMethodException ignored) {}
        HAS_PREPROCESS_TEXT = found;
    }

    /** Only call when {@link #HAS_PREPROCESS_TEXT} is true. */
    public static String preprocessText(String s) {
        return FontRendering.preprocessText(s);
    }
}
