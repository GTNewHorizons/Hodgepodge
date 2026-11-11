package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(ItemSoup.class)
public class MixinItemSoup extends ItemFood {

    // dummy constructor
    private MixinItemSoup(int p_i45339_1_, float p_i45339_2_, boolean p_i45339_3_) {
        super(p_i45339_1_, p_i45339_2_, p_i45339_3_);
    }

    // Fix deleting stack (>1) when eating mushroom stew

    @Inject(at = @At("HEAD"), method = "onEaten")
    private void hodgepodge$fixStackDeletion(ItemStack p_77654_1_, World p_77654_2_, EntityPlayer p_77654_3_,
            CallbackInfoReturnable<ItemStack> cir) {
        if (TweaksConfig.allowEatingFoodInCreative && p_77654_3_.capabilities.isCreativeMode) return;

        ItemStack emptyBowl = new ItemStack(Items.bowl);

        if (!p_77654_3_.inventory.addItemStackToInventory(emptyBowl)) {
            p_77654_3_.dropPlayerItemWithRandomChoice(emptyBowl, true);
        }
    }

    @Redirect(
            allow = 1,
            at = @At(target = "(Lnet/minecraft/item/Item;)Lnet/minecraft/item/ItemStack;", value = "NEW"),
            method = "onEaten",
            slice = @Slice(
                    from = @At(
                            target = "Lnet/minecraft/item/ItemFood;onEaten(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;",
                            value = "INVOKE")))
    private ItemStack hodgepodge$returnStew(Item p_i1879_1_, @Local(argsOnly = true) ItemStack p_77654_1_) {
        return p_77654_1_;
    }
}
