package com.mitchej123.hodgepodge.mixins.biomesoplenty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import biomesoplenty.api.content.BOPCBlocks;
import biomesoplenty.common.blocks.BlockBOPSapling;
import biomesoplenty.common.world.features.trees.WorldGenBOPTaiga2;
import biomesoplenty.common.world.features.trees.WorldGenBOPTaiga3;
import net.minecraft.block.Block;
import net.minecraft.world.World;

@Mixin(BlockBOPSapling.class)
public class MixinBlockBOPSapling {
    
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lbiomesoplenty/common/world/features/trees;<init>(Lnet/minecraft/block/Block;Lnet/minecraft/block/Block;IIZIIII)V", remap = false),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "func_149878_d")
    public void growTree(World world, int x, int y, int z, Random random, CallbackInfo ci, int meta, Object obj, int rnd) {
        
        // The code below should be called after
        // obj = new WorldGenBOPTaiga2(BOPCBlocks.logs1, BOPCBlocks.leaves2, 3, 1, false, 10, 10, 5, 4);
        
        if(isBigFirPattern(world, x, y, z)) {
            // The pattern is correct and the middle sapling should grow -> generate big fir tree
            obj = new WorldGenBOPTaiga3(BOPCBlocks.logs1, BOPCBlocks.leaves2, 3, 1, false, 35, 10, 0, 4);
        }
        else if(isBigFirNeighbor(world, x, y, z)) {
            // The pattern is correct and any other sapling than the middle one should grow -> generate nothing
            obj = null;
        }
        else if(random.nextInt(10) == 0) {
            // 10% chance to generate a "medium" size fir tree
            obj = new WorldGenBOPTaiga2(BOPCBlocks.logs1, BOPCBlocks.leaves2, 3, 1, false, 20, 15, 4, 4);
        }
        // I all if statements above failed, obj is not overridden and a small fir tree will be generated
    }
    
    private static boolean isFirSapling(Block block, int meta) {
        return BOPCBlocks.saplings == block && meta == 6;
    }
    
    private static boolean isBigFirPattern(World world, int x, int y, int z) {
        if(world == null || !isFirSapling(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z))) return false;
        
        for(Pair<Block, Integer> neighborBlock : getNeighbors(world, x, y, z)) {
            if(!isFirSapling(neighborBlock.getLeft(), neighborBlock.getRight())) return false;
        }
        
        return true;
    }
    
    private static boolean isBigFirNeighbor(World world, int x, int y, int z) {
        if(world == null) return false;
        return isBigFirPattern(world, x - 1, y, z)
                || isBigFirPattern(world, x + 1, y, z)
                || isBigFirPattern(world, x, y, z - 1)
                || isBigFirPattern(world, x, y, z + 1);
    }
    
    private static List<Pair<Block, Integer>> getNeighbors(World world, int x, int y, int z){
        List<Pair<Block, Integer>> result = new ArrayList<>();
        
        if(world != null) {
            result.add(Pair.of(world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z)));
            result.add(Pair.of(world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z)));
            result.add(Pair.of(world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1)));
            result.add(Pair.of(world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1)));
        }
        
        return result;
    }

}
