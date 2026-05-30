package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Random;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mitchej123.hodgepodge.util.ColorFormatUtils;

import cpw.mods.fml.common.Loader;

/**
 * Renders a basic drop shadow for segments marked with {@code §u} when Angelica is absent. Also strips {@code §u} from
 * text before vanilla processes it, since vanilla treats {@code §u} as a color code (mapped to white).
 *
 * When Angelica IS installed, its {@code @Inject} on {@code drawString} cancels before {@code renderStringAtPos} is
 * reached, so the shadow pre-pass never fires. The stripping still applies for the rare display-list fallback path
 * where vanilla's renderer is used even with Angelica present.
 */
@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer_FallbackShadow {

    @Shadow
    protected float posX;

    @Shadow
    protected float posY;

    @Shadow
    private boolean unicodeFlag;

    @Shadow
    private int[] colorCode;

    @Shadow
    protected int[] charWidth;

    @Shadow
    public Random fontRandom;

    @Shadow
    private float alpha;

    @Shadow
    private float red;

    @Shadow
    private float blue;

    @Shadow
    private float green;

    @Shadow
    protected abstract void setColor(float r, float g, float b, float a);

    @Shadow
    protected abstract float renderDefaultChar(int index, boolean italic);

    @Shadow
    protected abstract float renderUnicodeChar(char c, boolean italic);

    @Unique
    private static final String hodgepodge$FORMAT_CODES = "0123456789abcdefklmnor";

    @Unique
    private static Boolean hodgepodge$angelicaLoaded;

    @WrapMethod(method = "renderStringAtPos")
    private void hodgepodge$wrapRenderStringAtPos(String text, boolean shadow, Operation<Void> original) {
        if (text == null || !hodgepodge$hasShadowCode(text)) {
            original.call(text, shadow);
            return;
        }

        if (!shadow && !hodgepodge$isAngelicaLoaded()) {
            hodgepodge$shadowPrePass(text);
        }

        original.call(hodgepodge$stripShadowCode(text), shadow);
    }

    @Unique
    private static boolean hodgepodge$isAngelicaLoaded() {
        if (hodgepodge$angelicaLoaded == null) {
            hodgepodge$angelicaLoaded = Loader.isModLoaded("angelica");
        }
        return hodgepodge$angelicaLoaded;
    }

    @Unique
    private static boolean hodgepodge$hasShadowCode(String text) {
        int idx = 0;
        while ((idx = text.indexOf('§', idx)) != -1) {
            if (idx + 1 < text.length() && Character.toLowerCase(text.charAt(idx + 1)) == 'u') {
                return true;
            }
            idx++;
        }
        return false;
    }

    @Unique
    private static String hodgepodge$stripShadowCode(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '§' && i + 1 < text.length() && Character.toLowerCase(text.charAt(i + 1)) == 'u') {
                // §u, optionally trailed by a §x§R§R§G§G§B§B custom shadow colour — strip both as one unit so
                // vanilla never sees the §x payload (it would otherwise treat it as black text).
                if (i + 2 + ColorFormatUtils.SECTION_X_SEQ_LEN <= text.length() && text.charAt(i + 2) == '§'
                        && Character.toLowerCase(text.charAt(i + 3)) == 'x'
                        && ColorFormatUtils.parseRgbFromSectionX(text, i + 2) != -1) {
                    i += 1 + ColorFormatUtils.SECTION_X_SEQ_LEN;
                } else {
                    i++;
                }
                continue;
            }
            sb.append(text.charAt(i));
        }
        return sb.toString();
    }

    // Vanilla's default.png glyph-position string (FontRenderer#renderCharAtPos). A char's INDEX in this string is its
    // cell in default.png; renderDefaultChar takes that cell index, NOT the raw char code. Copied byte-exact from the
    // decompiled FontRenderer so the shadow pass dispatches glyphs identically to the main text.
    @Unique
    private static final String hodgepodge$FONT_POS = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000";

    /**
     * Resolves the default.png cell a char draws from, mirroring vanilla {@code FontRenderer#renderCharAtPos}: a char's
     * INDEX in {@link #hodgepodge$FONT_POS} is its cell. Returns -1 for chars not in the default font (drawn from the
     * unicode pages instead). Under the {@code §k} obfuscated style, the cell is swapped for a random same-width one
     * (as vanilla does in {@code renderStringAtPos}) so the shadow tracks the scrambling main text instead of ghosting
     * the literal characters behind it. The result is resolved once per char and reused for the bold overlay, matching
     * vanilla which renders bold from the same (already randomized) cell.
     */
    @Unique
    private int hodgepodge$resolveShadowCell(char c, boolean random) {
        if (this.unicodeFlag) return -1;
        int idx = hodgepodge$FONT_POS.indexOf(c);
        if (idx == -1) return -1;
        if (random) {
            int k;
            do {
                k = this.fontRandom.nextInt(this.charWidth.length);
            } while (this.charWidth[idx] != this.charWidth[k]);
            idx = k;
        }
        return idx;
    }

    /**
     * Draws one shadow glyph at the current pos. {@code cell} comes from {@link #hodgepodge$resolveShadowCell}: -1
     * means draw from the unicode pages, otherwise that default.png cell. Space draws nothing (vanilla special-case) —
     * routing it through {@code renderUnicodeChar} would paint a missing-glyph box.
     */
    @Unique
    private void hodgepodge$drawShadowGlyph(char c, int cell, boolean italic) {
        if (c == ' ') return;
        if (cell != -1) {
            renderDefaultChar(cell, italic);
        } else {
            renderUnicodeChar(c, italic);
        }
    }

    @Unique
    private void hodgepodge$shadowPrePass(String text) {
        float startPosX = this.posX;
        float startPosY = this.posY;

        float trackX = startPosX;
        boolean shadowActive = false;
        boolean customShadowActive = false;
        int customShadowColor = 0;
        int curColor = ((int) (this.red * 255.0f) << 16) | ((int) (this.blue * 255.0f) << 8)
                | ((int) (this.green * 255.0f));
        int initialColor = curColor;
        boolean curBold = false;
        boolean curItalic = false;
        boolean curRandom = false;
        float shadowOffset = this.unicodeFlag ? 0.5f : 1.0f;
        int lastShadowColor = -1;

        // Shadow glyphs are coplanar (Z=0) with the main text drawn afterwards but offset ~1px in X/Y. In 3D (signs,
        // nametags) perspective makes those coplanar same-depth quads z-fight into a flickering pixel-wide line as the
        // camera moves. Disable depth WRITES (not the depth TEST) for the shadow pass: the shadow stays correctly
        // occluded by world geometry but writes no depth, so the main text drawn afterwards always wins the overlap by
        // draw order. Correct in both 3D and the 2D GUI. polygonOffset is wrong here: text quads are view-parallel so
        // its slope term is ~0 and it either under-corrects or pushes the shadow into the plank behind it.
        final boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glDepthMask(false);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§' && i + 1 < text.length()) {
                char next = Character.toLowerCase(text.charAt(i + 1));

                if (next == 'u') {
                    // §u§x§R§R§G§G§B§B sets a custom shadow colour (used directly, undarkened, like Angelica);
                    // a bare §u toggles a shadow derived from the current text colour.
                    if (i + 2 + ColorFormatUtils.SECTION_X_SEQ_LEN <= text.length() && text.charAt(i + 2) == '§'
                            && Character.toLowerCase(text.charAt(i + 3)) == 'x') {
                        int rgb = ColorFormatUtils.parseRgbFromSectionX(text, i + 2);
                        if (rgb != -1) {
                            shadowActive = true;
                            customShadowActive = true;
                            customShadowColor = rgb;
                            i += 1 + ColorFormatUtils.SECTION_X_SEQ_LEN;
                            continue;
                        }
                    }
                    shadowActive = !shadowActive;
                    if (!shadowActive) customShadowActive = false;
                    i++;
                    continue;
                }

                int idx = hodgepodge$FORMAT_CODES.indexOf(next);
                if (idx < 16) {
                    curBold = false;
                    curItalic = false;
                    curRandom = false;
                    int colorIdx = (idx < 0 || idx > 15) ? 15 : idx;
                    curColor = this.colorCode[colorIdx];
                } else if (idx == 16) {
                    curRandom = true;
                } else if (idx == 17) {
                    curBold = true;
                } else if (idx == 20) {
                    curItalic = true;
                } else if (idx == 21) {
                    curBold = false;
                    curItalic = false;
                    curRandom = false;
                    shadowActive = false;
                    customShadowActive = false;
                    curColor = initialColor;
                }
                i++;
                continue;
            }

            int charW = ((FontRenderer) (Object) this).getCharWidth(c);

            if (shadowActive && charW > 0) {
                int shadowRgb = customShadowActive ? customShadowColor : ((curColor & 0xFCFCFC) >> 2);
                if (shadowRgb != lastShadowColor) {
                    float r = (float) ((shadowRgb >> 16) & 0xFF) / 255.0f;
                    float g = (float) ((shadowRgb >> 8) & 0xFF) / 255.0f;
                    float b = (float) (shadowRgb & 0xFF) / 255.0f;
                    setColor(r, g, b, this.alpha);
                    lastShadowColor = shadowRgb;
                }

                int shadowCell = hodgepodge$resolveShadowCell(c, curRandom);

                this.posX = trackX + shadowOffset;
                this.posY = startPosY + shadowOffset;
                hodgepodge$drawShadowGlyph(c, shadowCell, curItalic);

                if (curBold) {
                    this.posX = trackX + shadowOffset + 1.0f;
                    this.posY = startPosY + shadowOffset;
                    hodgepodge$drawShadowGlyph(c, shadowCell, curItalic);
                }
            }

            trackX += (float) charW;
            if (curBold && charW > 0) {
                trackX += 1.0f;
            }
        }

        GL11.glDepthMask(prevDepthMask);

        this.posX = startPosX;
        this.posY = startPosY;
        setColor(this.red, this.blue, this.green, this.alpha);
    }
}
