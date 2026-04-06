package com.mitchej123.hodgepodge.mixins.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;

import com.mitchej123.hodgepodge.config.MemoryConfig;
import com.mitchej123.hodgepodge.mixins.early.memory.ItemRendererAccessor;

public final class AfterClientExitWorldHook {

    /**
     * This gets called when the client loads a null world, and the previous world was not null, e.g. when exiting to
     * main menu
     */
    public static void run(Minecraft mc) {
        if (MemoryConfig.leaks.fixEntityRendererItemRendererLeak) {
            EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
            if (entityRenderer != null) {
                ((ItemRendererAccessor) entityRenderer.itemRenderer).setItemToRender(null);
            }
        }
        if (MemoryConfig.leaks.fixRenderersWorldLeak) {
            if (mc.renderGlobal != null) {
                mc.renderGlobal.setWorldAndLoadRenderers(null);
            }

            if (mc.effectRenderer != null) {
                mc.effectRenderer.clearEffects(null);
            }
        }
    }
}
