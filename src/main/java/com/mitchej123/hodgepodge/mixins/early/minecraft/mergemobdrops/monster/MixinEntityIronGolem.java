package com.mitchej123.hodgepodge.mixins.early.minecraft.mergemobdrops.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityIronGolem.class)
public abstract class MixinEntityIronGolem extends Entity {

    /**
     * @author nimuck
     * @reason Batch ItemStacks instead of looping, though a lootingLevel don't affect them
     */
    @Overwrite
    protected void dropFewItems(boolean recentlyHit, int lootingLevel) {
        int poppyCount = this.rand.nextInt(3);
        if (poppyCount > 0) {
            this.func_145778_a(Item.getItemFromBlock(Blocks.red_flower), poppyCount, 0.0F);
        }

        int ironCount = 3 + this.rand.nextInt(3);
        if (ironCount > 0) {
            this.dropItem(Items.iron_ingot, ironCount);
        }
    }

    private MixinEntityIronGolem(World worldIn) {
        super(worldIn);
    }
}
