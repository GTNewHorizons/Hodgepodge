package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = ShapedOreRecipe.class)
public class MixinShapedOreRecipe {

    @Shadow(remap = false)
    private int width;

    @Shadow(remap = false)
    private int height;

    @Shadow(remap = false)
    private Object[] input;

    @Shadow(remap = false)
    private boolean mirrored;

    @Unique
    private int hodgepodge$activeSize = -1;

    /**
     * @author kuba6000
     * @reason Optimize ShapedOreRecipe
     */
    @Overwrite
    public boolean matches(InventoryCrafting inv, World world) {
        if (hodgepodge$activeSize == -1) {
            hodgepodge$activeSize = 0;
            for (int i = 0; i < width * height; i++) {
                if (input[i] != null) {
                    hodgepodge$activeSize++;
                    break;
                }
            }
        }

        int invSize = inv.getSizeInventory();
        int wsize = (int) Math.sqrt(invSize);

        // some basic checks to avoid item comparing
        if (height > wsize || width > wsize) {
            return false;
        }
        int invActiveSize = 0;
        for (int i = 0; i < invSize; i++) {
            if (inv.getStackInSlot(i) != null) {
                invActiveSize++;
                if (invActiveSize > hodgepodge$activeSize) {
                    return false;
                }
            }
        }

        if (invActiveSize != hodgepodge$activeSize) {
            return false;
        }

        for (int i = 0; i < wsize - width + 1; i++) {
            for (int j = 0; j < wsize - height + 1; j++) {
                if (checkMatch(inv, i, j, true)) {
                    return true;
                }
                if (mirrored && checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Shadow(remap = false)
    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        throw new RuntimeException("Mixin failed to apply");
    }

}
