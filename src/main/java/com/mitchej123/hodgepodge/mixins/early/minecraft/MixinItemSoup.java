package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemSoup.class)
public class MixinItemSoup extends ItemFood {

    // dummy constructor
    private MixinItemSoup(int p_i45339_1_, float p_i45339_2_, boolean p_i45339_3_) {
        super(p_i45339_1_, p_i45339_2_, p_i45339_3_);
    }

    /**
     * @author rot13
     * @reason Fix deleting stack (>1) when eating mushroom stew
     */
    @Overwrite
    public ItemStack onEaten(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_) {
        ItemStack emptyBowl = new ItemStack(Items.bowl);

        if (!p_77654_3_.inventory.addItemStackToInventory(emptyBowl)) {
            p_77654_3_.dropPlayerItemWithRandomChoice(emptyBowl, true);
        }

        return super.onEaten(p_77654_1_, p_77654_2_, p_77654_3_);
    }
}
