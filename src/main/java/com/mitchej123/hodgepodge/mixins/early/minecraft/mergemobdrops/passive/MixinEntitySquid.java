package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.passive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntitySquid.class)
public abstract class MixinEntitySquid extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int inkCount = this.rand.nextInt(3 + lootingLevel) + 1;
        if (inkCount > 0) {
            this.entityDropItem(new ItemStack(Items.dye, inkCount, 0), 0.0F);
        }
    }

    private MixinEntitySquid(World worldIn) {
        super(worldIn);
    }
}
