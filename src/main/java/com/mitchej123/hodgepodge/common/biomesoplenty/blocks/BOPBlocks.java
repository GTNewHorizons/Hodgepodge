package com.mitchej123.hodgepodge.common.biomesoplenty.blocks;

import java.util.EnumMap;

import net.minecraft.block.Block;

public class BOPBlocks {

    public enum WoodTypes {
        sacredoak,
        cherry,
        dark,
        fir,
        ethereal,
        magic,
        mangrove,
        palm,
        redwood,
        willow,
        bamboothatching,
        pine,
        hell_bark,
        jacaranda,
        mahogany
    }

    public static final EnumMap<WoodTypes, Block> fences = new EnumMap<>(WoodTypes.class);
    public static final EnumMap<WoodTypes, Block> fence_gates = new EnumMap<>(WoodTypes.class);
}
