package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.mixins.early.minecraft.AccessorGuiContainer;
import com.mitchej123.hodgepodge.mixins.early.minecraft.AccessorMinecraft;

import thaumcraft.client.lib.UtilsFX;

@Mixin(value = UtilsFX.class, remap = false)
public class MixinUtilsFX {

    @Unique
    private static final ResourceLocation hp$particleTextures = new ResourceLocation("textures/particle/particles.png");

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static float getEquippedProgress(ItemRenderer ir) {
        return ir.equippedProgress;
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static float getPrevEquippedProgress(ItemRenderer ir) {
        return ir.prevEquippedProgress;
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static Timer getTimer(Minecraft mc) {
        return ((AccessorMinecraft) mc).getTimer();
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static int getGuiXSize(GuiContainer gui) {
        return ((AccessorGuiContainer) gui).getXSize();
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static int getGuiYSize(GuiContainer gui) {
        return ((AccessorGuiContainer) gui).getYSize();
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static float getGuiZLevel(Gui gui) {
        return gui.zLevel;
    }

    /**
     * @author Sisyphussy
     * @reason inefficient reflection
     */
    @Overwrite
    public static ResourceLocation getParticleTexture() {
        return hp$particleTextures;
    }
}
