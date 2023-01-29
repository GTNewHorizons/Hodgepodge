package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraftforge.client.event.EntityViewRenderEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import biomesoplenty.client.fog.FogHandler;

import com.mitchej123.hodgepodge.client.biomesoplenty.BOPFogHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(FogHandler.class)
public class MixinFogHandler {

    /**
     * @author glowredman, TataTawa
     * @reason It is not useful to calculate 1600 times per frame only to get newest fogX and fogZ
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        BOPFogHandler.onRenderFog(event, (FogHandler) (Object) this);
    }
}
