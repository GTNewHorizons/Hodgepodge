package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_MergeMobDrops extends Entity {

    @Shadow
    protected abstract Item getDropItem();

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        Item item = this.getDropItem();

        if (item == null) {
            return;
        }

        int count = this.rand.nextInt(3);
        if (lootingLevel > 0) {
            count += this.rand.nextInt(lootingLevel + 1);
        }

        if (count > 0) {
            this.dropItem(item, count);
        }
    }

    private MixinEntityLiving_MergeMobDrops(World worldIn) {
        super(worldIn);
    }
}
