package com.mitchej123.hodgepodge.mixins.hooks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import com.mitchej123.hodgepodge.config.MemoryConfig;
import com.mitchej123.hodgepodge.mixins.early.memory.ItemRendererAccessor;

public final class ClientLeaksCleaningHook {

    /**
     * This gets called when the client loads a null world, and the previous world was not null, e.g. when exiting to
     * main menu
     */
    public static void onClientExitToMainMenu(Minecraft mc) {
        if (MemoryConfig.leaks.fixRenderersWorldLeak) {
            if (mc.renderGlobal != null) {
                mc.renderGlobal.setWorldAndLoadRenderers(null);
            }
            if (mc.effectRenderer != null) {
                mc.effectRenderer.clearEffects(null);
            }
        }
        if (MemoryConfig.leaks.fixTileEntityRendererWorldLeak) {
            TileEntityRendererDispatcher.instance.field_147551_g = null;
            TileEntityRendererDispatcher.instance.func_147543_a(null);
        }
        if (MemoryConfig.leaks.fixRenderManagerWorldLeak) {
            RenderManager.instance.renderEngine = null;
            RenderManager.instance.set(null);
            RenderManager.instance.livingPlayer = null;
            RenderManager.instance.field_147941_i = null;
        }
        if (MemoryConfig.leaks.fixPlayerControllerWorldLeak) {
            mc.playerController = null;
        }
    }

    /**
     * This runs everytime a client world gets unloaded, e.g. when switching dimensions and when exiting to main menu
     */
    public static void onClientWorldUnload() {
        if (MemoryConfig.leaks.fixEntityRendererItemRendererLeak) {
            EntityRenderer entityRenderer = Minecraft.getMinecraft().entityRenderer;
            if (entityRenderer != null) {
                ((ItemRendererAccessor) entityRenderer.itemRenderer).setItemToRender(null);
            }
        }
        if (MemoryConfig.leaks.fixRenderBlocksWorldLeak) {
            RenderBlocks.getInstance().blockAccess = null;
        }
        if (MemoryConfig.leaks.fixPointedEntityLeak) {
            final Minecraft mc = Minecraft.getMinecraft();
            mc.pointedEntity = null;
            mc.objectMouseOver = null;
        }
    }
}
