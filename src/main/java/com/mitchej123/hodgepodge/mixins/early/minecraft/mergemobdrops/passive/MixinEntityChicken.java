package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityChicken.class)
public abstract class MixinEntityChicken extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int featherCount = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingLevel);
        if (featherCount > 0) {
            this.dropItem(Items.feather, featherCount);
        }

        Item chickenItem = this.isBurning() ? Items.cooked_chicken : Items.chicken;
        this.dropItem(chickenItem, 1);
    }

    private MixinEntityChicken(World worldIn) {
        super(worldIn);
    }
}
