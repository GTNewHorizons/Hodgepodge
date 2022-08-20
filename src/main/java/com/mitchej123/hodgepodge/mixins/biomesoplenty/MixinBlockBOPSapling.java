package com.mitchej123.hodgepodge.mixins.biomesoplenty;

import biomesoplenty.api.content.BOPCBlocks;
import biomesoplenty.common.blocks.BlockBOPSapling;
import biomesoplenty.common.world.features.trees.WorldGenBOPTaiga2;
import biomesoplenty.common.world.features.trees.WorldGenBOPTaiga3;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BlockBOPSapling.class)
public class MixinBlockBOPSapling {

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "func_149878_d")
    public void growTree(
            World world, int x, int y, int z, Random random, CallbackInfo ci, int meta, Object obj, int rnd) {

        // We only care about fir saplings
        if (meta != 6) return;

        if (isBigFirPattern(world, x, y, z)) {
            // The pattern is correct and the middle sapling should grow -> generate big fir tree
            obj = new WorldGenBOPTaiga3(BOPCBlocks.logs1, BOPCBlocks.leaves2, 3, 1, false, 35, 10, 0, 4);
        } else if (isBigFirNeighbor(world, x, y, z)) {
            // The pattern is correct and any other sapling than the middle one should grow -> generate nothing
            ci.cancel();
        } else if (random.nextInt(10) == 0) {
            // 10% chance to generate a "medium" size fir tree
            obj = new WorldGenBOPTaiga2(BOPCBlocks.logs1, BOPCBlocks.leaves2, 3, 1, false, 20, 15, 4, 4);
        }
        // I all if statements above failed, obj is not overridden and a small fir tree will be generated

        // We can't return because obj has been modified, so the rest of the code will be executed here:
        world.setBlockToAir(x, y, z);

        if (!((WorldGenerator) obj).generate(world, random, x, y, z)) {
            world.setBlock(x, y, z, BOPCBlocks.saplings, meta, 2);
        }

        ci.cancel();
    }

    private static boolean isFirSapling(Block block, int meta) {
        return BOPCBlocks.saplings == block && meta == 6;
    }

    private static boolean isBigFirPattern(World world, int x, int y, int z) {
        if (world == null || !isFirSapling(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z))) return false;

        for (Pair<Block, Integer> neighborBlock : getNeighbors(world, x, y, z)) {
            if (!isFirSapling(neighborBlock.getLeft(), neighborBlock.getRight())) return false;
        }

        return true;
    }

    private static boolean isBigFirNeighbor(World world, int x, int y, int z) {
        if (world == null) return false;
        return isBigFirPattern(world, x - 1, y, z)
                || isBigFirPattern(world, x + 1, y, z)
                || isBigFirPattern(world, x, y, z - 1)
                || isBigFirPattern(world, x, y, z + 1);
    }

    private static List<Pair<Block, Integer>> getNeighbors(World world, int x, int y, int z) {
        List<Pair<Block, Integer>> result = new ArrayList<>();

        if (world != null) {
            result.add(Pair.of(world.getBlock(x - 1, y, z), world.getBlockMetadata(x - 1, y, z)));
            result.add(Pair.of(world.getBlock(x + 1, y, z), world.getBlockMetadata(x + 1, y, z)));
            result.add(Pair.of(world.getBlock(x, y, z - 1), world.getBlockMetadata(x, y, z - 1)));
            result.add(Pair.of(world.getBlock(x, y, z + 1), world.getBlockMetadata(x, y, z + 1)));
        }

        return result;
    }
}
