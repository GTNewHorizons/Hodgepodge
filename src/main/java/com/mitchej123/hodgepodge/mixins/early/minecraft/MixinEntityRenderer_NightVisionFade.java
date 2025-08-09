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
     * "https://git.sr.ht/~nnnotcharsy/exp5core/tree/master/item/src/main/java/dev/charsy/exp5core/mixins/early/minecraft/MixinEntityRenderer.java">exp5core</a>
     * 
     * @author Charsy89
     * @reason When the Night Vision effect is wearing off, the surrounding light is made dark and bright in a sine-wave
     *         pattern. This can be disorienting for some users, so here we make it "fade out" instead.
     */
    @Overwrite
    private float getNightVisionBrightness(EntityPlayer plr, float renderPartialTicks) {
        int duration = plr.getActivePotionEffect(Potion.nightVision).getDuration();
        return duration > TweaksConfig.fadeNightVisionDuration ? 1.0F
                : (float) duration / (float) TweaksConfig.fadeNightVisionDuration;
    }
}
