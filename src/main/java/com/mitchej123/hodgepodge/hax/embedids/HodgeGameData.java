package com.mitchej123.hodgepodge.hax.embedids;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameData;

public class HodgeGameData extends GameData {

    public HodgeGameData() {
        super();

        try {
            // We grab these fields with reflection to avoid javac from inlining them.
            // NEIDs and EIDs may want to increase them at runtime!
            iBlockRegistry = new HodgeNamespacedRegistry<>(
                    "minecraft:air",
                    GameData.class.getDeclaredField("MAX_BLOCK_ID").getInt(null),
                    GameData.class.getDeclaredField("MIN_BLOCK_ID").getInt(null),
                    Block.class,
                    '\u0001');
            iItemRegistry = new HodgeNamespacedRegistry<>(
                    null,
                    GameData.class.getDeclaredField("MAX_ITEM_ID").getInt(null),
                    GameData.class.getDeclaredField("MIN_ITEM_ID").getInt(null),
                    Item.class,
                    '\u0002');
        } catch (IllegalAccessException | NoSuchFieldException e) {
            // If this fails, you have *much* bigger issues.
            throw new RuntimeException(e);
        }
    }
}
