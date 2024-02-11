package com.mitchej123.hodgepodge.mixins.early.minecraft.textures.client;

import net.minecraft.client.renderer.texture.TextureUtil;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.client.textures.Mipmaps;

@Mixin(TextureUtil.class)
public class MixinTextureUtil {

    /**
     * @author UltraHex
     * @reason Check entire texture for transparency.
     * @reason Don't divide by zero for small textures.
     */
    @Overwrite
    public static int[][] generateMipmapData(int levels, int size, int[][] texture) {
        int[][] mipmaps = new int[levels + 1][];
        mipmaps[0] = texture[0];

        if (levels > 0) {
            boolean transparent = false;

            for (int l = 0; l < texture[0].length; ++l) {
                if (texture[0][l] >> 24 == 0) {
                    transparent = true;
                    break;
                }
            }

            for (int level = 1; level <= levels; ++level) {
                if (texture[level] != null) {
                    mipmaps[level] = texture[level];
                } else {
                    int[] prevLevel = mipmaps[level - 1];

                    int width = size >> level;
                    if (width <= 0) {
                        mipmaps[level] = prevLevel;
                        continue;
                    }

                    int[] mipmap = new int[prevLevel.length >> 2];

                    int height = mipmap.length / width;
                    int prevWidth = width << 1;

                    for (int x = 0; x < width; ++x) {
                        for (int y = 0; y < height; ++y) {
                            int prevPos = 2 * (x + y * prevWidth);
                            mipmap[x + y * width] = func_147943_a(
                                    prevLevel[prevPos],
                                    prevLevel[prevPos + 1],
                                    prevLevel[prevPos + prevWidth],
                                    prevLevel[prevPos + 1 + prevWidth],
                                    transparent);
                        }
                    }

                    mipmaps[level] = mipmap;
                }
            }
        }

        return mipmaps;
    }

    /**
     * @author SuperCoder79
     * @reason Rewrite mipmap color math to use memoized value array instead of using Math.pow directly
     */
    @Overwrite
    private static int func_147943_a(int one, int two, int three, int four, boolean alpha) {
        if (!alpha) {
            int a = Mipmaps.getColorComponent(one, two, three, four, 24);
            int r = Mipmaps.getColorComponent(one, two, three, four, 16);
            int g = Mipmaps.getColorComponent(one, two, three, four, 8);
            int b = Mipmaps.getColorComponent(one, two, three, four, 0);
            return a << 24 | r << 16 | g << 8 | b;
        } else {
            int n = 0;

            float a = 0.0F;
            float r = 0.0F;
            float g = 0.0F;
            float b = 0.0F;
            if (one >> 24 != 0) {
                a += Mipmaps.get(one >> 24);
                r += Mipmaps.get(one >> 16);
                g += Mipmaps.get(one >> 8);
                b += Mipmaps.get(one >> 0);
                n++;
            }

            if (two >> 24 != 0) {
                a += Mipmaps.get(two >> 24);
                r += Mipmaps.get(two >> 16);
                g += Mipmaps.get(two >> 8);
                b += Mipmaps.get(two >> 0);
                n++;
            }

            if (three >> 24 != 0) {
                a += Mipmaps.get(three >> 24);
                r += Mipmaps.get(three >> 16);
                g += Mipmaps.get(three >> 8);
                b += Mipmaps.get(three >> 0);
                n++;
            }

            if (four >> 24 != 0) {
                a += Mipmaps.get(four >> 24);
                r += Mipmaps.get(four >> 16);
                g += Mipmaps.get(four >> 8);
                b += Mipmaps.get(four >> 0);
                n++;
            }

            a /= 4.0F;

            if (n != 0) {
                r /= n;
                g /= n;
                b /= n;
            }

            int ia = (int) (Math.pow(a, 0.45454545454545453) * 255.0);
            int ir = (int) (Math.pow(r, 0.45454545454545453) * 255.0);
            int ig = (int) (Math.pow(g, 0.45454545454545453) * 255.0);
            int ib = (int) (Math.pow(b, 0.45454545454545453) * 255.0);

            return ia << 24 | ir << 16 | ig << 8 | ib;
        }
    }
}
