package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemEnderPearl.class)
public abstract class MixinItemEnderPearl extends Item {

    /**
     * @author Colen
     * @reason Enables enderpearls to be used in creative by removing check.
     */
    @Overwrite
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        itemStackIn.stackSize--;
        worldIn.playSoundAtEntity(player, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityEnderPearl(worldIn, player));
        }

        return itemStackIn;
    }

}
