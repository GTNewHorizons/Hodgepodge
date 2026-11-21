package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySkeleton.class)
public abstract class MixinEntitySkeleton extends EntityMob {

    @Shadow
    public abstract int getSkeletonType();

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (this.getSkeletonType() == 1) {
            int dropCount = this.rand.nextInt(3 + lootingLevel) - 1;
            if (dropCount > 0) {
                this.dropItem(Items.coal, dropCount);
            }
        } else {
            int dropCount = this.rand.nextInt(3 + lootingLevel);
            if (dropCount > 0) {
                this.dropItem(Items.arrow, dropCount);
            }
        }

        int boneCount = this.rand.nextInt(3 + lootingLevel);
        if (boneCount > 0) {
            this.dropItem(Items.bone, boneCount);
        }
    }

    private MixinEntitySkeleton(World worldIn) {
        super(worldIn);
    }
}
