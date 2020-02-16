package com.mitchej123.hodgepodge.core.mixin.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RandomPositionGenerator.class)
public class MixinRandomPositionGenerator {
    /**
     * @author mitchej123
     * @reason Fix north/west bias
     */
    @Overwrite()
    private static Vec3 findRandomTargetBlock(EntityCreature p_75462_0_, int p_75462_1_, int p_75462_2_, Vec3 p_75462_3_) {
        System.out.println("EFFF OFF!");
        return null;
    }
}
