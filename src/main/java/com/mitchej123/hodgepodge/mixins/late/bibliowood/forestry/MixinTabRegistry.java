package com.mitchej123.hodgepodge.mixins.late.bibliowood.forestry;

import net.minecraft.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import cpw.mods.fml.common.registry.GameRegistry;
import jds.bibliowood.forestrywood.WoodsLoader;

@Mixin(WoodsLoader.class)
public class MixinTabRegistry {

    @Unique
    private static final int hodgepodge$acaciaMeta = 2;
    @Unique
    private static final int hodgepodge$balsaMeta = 11;
    @Unique
    private static final int hodgepodge$baobabMeta = 6;
    @Unique
    private static final int hodgepodge$cherryMeta = 15;
    @Unique
    private static final int hodgepodge$chestnutMeta = 4;
    @Unique
    private static final int hodgepodge$citrusMeta = 23;
    @Unique
    private static final int hodgepodge$ebonyMeta = 9;
    @Unique
    private static final int hodgepodge$greenheartMeta = 14;
    @Unique
    private static final int hodgepodge$kapokMeta = 8;
    @Unique
    private static final int hodgepodge$larchMeta = 0;
    @Unique
    private static final int hodgepodge$limeMeta = 3;
    @Unique
    private static final int hodgepodge$mahoeMeta = 16;
    @Unique
    private static final int hodgepodge$mahogamyMeta = 10;
    @Unique
    private static final int hodgepodge$mapleMeta = 22;
    @Unique
    private static final int hodgepodge$palmMeta = 18;
    @Unique
    private static final int hodgepodge$papayaMeta = 19;
    @Unique
    private static final int hodgepodge$pineMeta = 20;
    @Unique
    private static final int hodgepodge$plumMeta = 21;
    @Unique
    private static final int hodgepodge$poplarMeta = 17;
    @Unique
    private static final int hodgepodge$sequoiaMeta = 7;
    @Unique
    private static final int hodgepodge$teakMeta = 1;
    @Unique
    private static final int hodgepodge$walnutMeta = 13;
    @Unique
    private static final int hodgepodge$wengeMeta = 5;
    @Unique
    private static final int hodgepodge$willowMeta = 12;

    @Unique
    private static final int[] hodgepodge$correctedMetas = new int[] { hodgepodge$acaciaMeta, hodgepodge$balsaMeta,
            hodgepodge$baobabMeta, hodgepodge$cherryMeta, hodgepodge$chestnutMeta, hodgepodge$citrusMeta,
            hodgepodge$ebonyMeta, hodgepodge$greenheartMeta, hodgepodge$kapokMeta, hodgepodge$larchMeta,
            hodgepodge$limeMeta, hodgepodge$mahoeMeta, hodgepodge$mahogamyMeta, hodgepodge$mapleMeta,
            hodgepodge$palmMeta, hodgepodge$papayaMeta, hodgepodge$pineMeta, hodgepodge$plumMeta, hodgepodge$poplarMeta,
            hodgepodge$sequoiaMeta, hodgepodge$teakMeta, hodgepodge$walnutMeta, hodgepodge$wengeMeta,
            hodgepodge$willowMeta };

    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), remap = false)
    private static Block[] hodgepodge$fixPlankID(Block[] metas) {
        final Block id = GameRegistry.findBlock("Forestry", "planks");
        return new Block[] { id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id,
                id };
    }

    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), remap = false)
    private static int[] hodgepodge$fixPlankMeta(int[] metas) {
        return hodgepodge$correctedMetas;
    }

    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private static Block[] hodgepodge$fixSlabsID(Block[] metas) {
        Block id = GameRegistry.findBlock("Forestry", "slabs");
        return new Block[] { id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id, id,
                id };
    }

    @ModifyVariable(method = "initRecipes", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private static int[] hodgepodge$fixSlabMeta(int[] metas) {
        return hodgepodge$correctedMetas;
    }
}
