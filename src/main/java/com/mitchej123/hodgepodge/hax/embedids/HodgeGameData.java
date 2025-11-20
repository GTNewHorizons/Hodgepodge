package com.mitchej123.hodgepodge.hax.embedids;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class HodgeGameData extends GameData {
    public HodgeGameData() {
        super();

        iBlockRegistry = new HodgeNamespacedRegistry<>("minecraft:air", MAX_BLOCK_ID, MIN_BLOCK_ID, Block.class,'\u0001');
        iItemRegistry = new HodgeNamespacedRegistry<>(null, MAX_ITEM_ID, MIN_ITEM_ID, Item.class,'\u0002');
    }
}
