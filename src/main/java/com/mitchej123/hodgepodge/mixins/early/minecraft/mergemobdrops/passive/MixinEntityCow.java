package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityCow.class)
public abstract class MixinEntityCow extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int leatherCount = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingLevel);
        if (leatherCount > 0) {
            this.dropItem(Items.leather, leatherCount);
        }

        int beefCount = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + lootingLevel);
        if (beefCount > 0) {
            Item beefItem = this.isBurning() ? Items.cooked_beef : Items.beef;
            this.dropItem(beefItem, beefCount);
        }
    }

    private MixinEntityCow(World worldIn) {
        super(worldIn);
    }
}
