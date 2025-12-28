package com.mitchej123.hodgepodge.mixins.late.bibliowood.forestry;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import cpw.mods.fml.common.registry.GameRegistry;
import jds.bibliowood.forestrywood.WoodsLoader;

@Mixin(WoodsLoader.class)
public class MixinTabRegistry {

    /**
     * @author DrParadox7
     * @reason Bibliowoods references multiple Forestry ID planks that no longer exists due to them all being unified
     *         into 1 block.
     */
    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE", ordinal = 0), remap = false)
    private static Block[] hodgepodge$fixPlankID(Block[] metas) {
        final Block id = GameRegistry.findBlock("Forestry", "planks");
        return new Block[] { id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id,
                id };
    }

    /**
     * @author DrParadox7
     * @reason Bibliowoods references multiple Forestry ID slabs that no longer exists due to them all being unified
     *         into 1 slab.
     */
    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private static Block[] hodgepodge$fixSlabsID(Block[] metas) {
        final Block id = GameRegistry.findBlock("Forestry", "slabs");
        return new Block[] { id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id,
                id };
    }

    /**
     * @author DrParadox7
     * @reason Apply corrected metas from unification refactors stated above. Meta Order: acacia, balsa, baobab, cherry,
     *         chestnut, citrus, ebony, greenheart, kapok, larch, lime, mahoe, mahogamy, maple, palm, papaya, pine,
     *         plum, poplar, sequoia, teak, walnut, wenge, willow
     */
    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), remap = false)
    private static int[] hodgepodge$fixMetas(int[] metas) {
        return new int[] { 2, 11, 6, 15, 4, 23, 9, 14, 8, 0, 3, 16, 10, 22, 18, 19, 20, 21, 17, 7, 1, 13, 5, 12 };
    }
}
