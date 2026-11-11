package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityBlaze.class)
public abstract class MixinEntityBlaze extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (recentlyHit) {
            int count = this.rand.nextInt(2 + lootingLevel);
            if (count > 0) {
                this.dropItem(Items.blaze_rod, count);
            }
        }
    }

    private MixinEntityBlaze(World worldIn) {
        super(worldIn);
    }
}
