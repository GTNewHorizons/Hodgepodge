package com.mitchej123.hodgepodge.common.biomesoplenty.blocks;

import biomesoplenty.BiomesOPlenty;
import biomesoplenty.api.content.BOPCBlocks;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.util.IIcon;

public class BlockBOPFenceGate extends BlockFenceGate {
    private static final String[] woodTypes = new String[] {"sacredoak", "cherry", "dark", "fir", "ethereal", "magic", "mangrove", "palm", "redwood", "willow", "bamboothatching", "pine", "hell_bark", "jacaranda", "mahogany"};
    private int typeIndex = 0;

    public BlockBOPFenceGate(String type) {
        super();
        this.setCreativeTab(BiomesOPlenty.tabBiomesOPlenty);
        for (int i = 0; i < woodTypes.length; i++) {
            if (woodTypes[i].equals(type)) {
                this.typeIndex = i;
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return BOPCBlocks.planks.getIcon(side, typeIndex);
    }
}
