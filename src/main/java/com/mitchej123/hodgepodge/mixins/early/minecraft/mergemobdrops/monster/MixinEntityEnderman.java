package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEnderman extends EntityLiving {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        Item item = this.getDropItem();
        if (item != null) {
            int count = this.rand.nextInt(2 + lootingLevel);
            if (count > 0) {
                this.dropItem(item, count);
            }
        }
    }

    private MixinEntityEnderman(World worldIn) {
        super(worldIn);
    }
}
