package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPigZombie.class)
public abstract class MixinEntityPigZombie extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int fleshCount = this.rand.nextInt(2 + lootingLevel);
        if (fleshCount > 0) {
            this.dropItem(Items.rotten_flesh, fleshCount);
        }

        int nuggetCount = this.rand.nextInt(2 + lootingLevel);
        if (nuggetCount > 0) {
            this.dropItem(Items.gold_nugget, nuggetCount);
        }
    }

    private MixinEntityPigZombie(World worldIn) {
        super(worldIn);
    }
}
