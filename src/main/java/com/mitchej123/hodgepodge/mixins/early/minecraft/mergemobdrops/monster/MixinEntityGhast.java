package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.init.Items;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityGhast.class)
public abstract class MixinEntityGhast extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int tearCount = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingLevel);
        if (tearCount > 0) {
            this.dropItem(Items.ghast_tear, tearCount);
        }

        int gunpowderCount = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingLevel);
        if (gunpowderCount > 0) {
            this.dropItem(Items.gunpowder, gunpowderCount);
        }
    }

    private MixinEntityGhast(World worldIn) {
        super(worldIn);
    }
}
