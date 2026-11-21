package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPig.class)
public abstract class MixinEntityPig extends Entity {

    @Shadow
    public abstract boolean getSaddled();

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int porkCount = this.rand.nextInt(3) + 1 + this.rand.nextInt(1 + lootingLevel);
        if (porkCount > 0) {
            Item porkItem = this.isBurning() ? Items.cooked_porkchop : Items.porkchop;
            this.dropItem(porkItem, porkCount);
        }

        if (this.getSaddled()) {
            this.dropItem(Items.saddle, 1);
        }
    }

    private MixinEntityPig(World worldIn) {
        super(worldIn);
    }
}
