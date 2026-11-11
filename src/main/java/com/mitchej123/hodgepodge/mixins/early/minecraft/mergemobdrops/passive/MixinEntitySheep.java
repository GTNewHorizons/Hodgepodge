package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntitySheep.class)
public abstract class MixinEntitySheep extends Entity {

    @Shadow
    public abstract boolean getSheared();

    @Shadow
    public abstract int getFleeceColor();

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        if (!this.getSheared()) {
            this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor()), 0.0F);
        }
    }

    private MixinEntitySheep(World worldIn) {
        super(worldIn);
    }
}
