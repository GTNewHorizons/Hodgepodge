package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer_NightVisionFade {

    /**
     * Originally from: <a href=
     * "https://github.com/Tesseract4D/BetterEffects/blob/master/src/main/java/mods/tesseract/bettereffects/FixesEffects.java">exp5core
     * </a>
     *
     * @author Tesseract4D
     * @reason When the Night Vision effect is wearing off, the surrounding light is made dark and bright in a sine-wave
     *         pattern. This can be disorienting for some users, so here we make it "fade out" instead.
     */
    // we intentionally widen the visibility of the method to avoid
    // crashing if a forge AT also widens the visibility of this method
    @SuppressWarnings("visibility")
    @Overwrite
    public float getNightVisionBrightness(EntityPlayer player, float partialTicks) {
        final int i = player.getActivePotionEffect(Potion.nightVision).getDuration();
        if (i > TweaksConfig.fadeNightVisionDuration) {
            return 1f;
        } else {
            return ((float) i - partialTicks) / (float) TweaksConfig.fadeNightVisionDuration;
        }
    }
}
