package com.mitchej123.hodgepodge.mixins.late.minefactoryreloaded;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import powercrystals.minefactoryreloaded.block.BlockRubberSapling;
import powercrystals.minefactoryreloaded.world.MineFactoryReloadedWorldGen;
import powercrystals.minefactoryreloaded.world.WorldGenMassiveTree;

@Mixin(BlockRubberSapling.class)
public class MixinBlockRubberSapling {

    /**
     * @author DrParadox7
     * @reason Swap Massive world-breaking tree to Mega, a still reasonably large but safe tree, should it still be
     *         obtained by chance.
     */
    @Redirect(
            method = "func_149878_d",
            at = @At(
                    value = "INVOKE",
                    target = "Lpowercrystals/minefactoryreloaded/world/WorldGenMassiveTree;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z"))
    private boolean hodgepodge$generateMassiveRubberTree(WorldGenMassiveTree instance, World world, Random random,
            int x, int y, int z) {
        return MineFactoryReloadedWorldGen.generateMegaRubberTree(world, random, x, y, z, true);
    }
}
