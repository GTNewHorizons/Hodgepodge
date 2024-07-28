package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(GameRegistry.class)
public class MixinGameRegistry {

    @ModifyExpressionValue(
            at = @At(
                    target = "Lcpw/mods/fml/common/registry/GameData;registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;)I",
                    value = "INVOKE"),
            method = "registerBlock(Lnet/minecraft/block/Block;Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/block/Block;",
            remap = false)
    private static int hodgepodge$registerBlockStats(int itemId, Block block, Class<? extends ItemBlock> itemclass,
            String name, Object[] itemCtorArgs, @Local ItemBlock i) {
        if (block.getEnableStats()) {
            StatCrafting statMine = hodgepodge$createAndRegisterStat("stat.mineBlock", i);
            StatList.mineBlockStatArray[itemId] = statMine;
            StatList.objectMineStats.add(statMine);
        }
        StatList.objectUseStats[itemId] = hodgepodge$createAndRegisterStat("stat.useItem", i);
        StatList.objectCraftStats[itemId] = hodgepodge$createAndRegisterStat("stat.craftItem", i);
        return itemId;
    }

    @ModifyExpressionValue(
            at = @At(
                    target = "Lcpw/mods/fml/common/registry/GameData;registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;)I",
                    value = "INVOKE"),
            method = "registerItem(Lnet/minecraft/item/Item;Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/item/Item;",
            remap = false)
    private static int hodgepodge$registerItemStats(int itemId, Item item, String name, String modId) {
        if (item.isDamageable()) {
            StatList.objectBreakStats[itemId] = hodgepodge$createAndRegisterStat("stat.breakItem", item);
        }
        StatCrafting statCraft = hodgepodge$createAndRegisterStat("stat.useItem", item);
        StatList.objectUseStats[itemId] = statCraft;
        if (!(item instanceof ItemBlock)) {
            StatList.itemStats.add(statCraft);
        }
        StatList.objectCraftStats[itemId] = hodgepodge$createAndRegisterStat("stat.craftItem", item);
        return itemId;
    }

    @Unique
    private static StatCrafting hodgepodge$createAndRegisterStat(String key, Item item) {
        StatCrafting stat = new StatCrafting(
                key + '.' + item.delegate.name(),
                new ChatComponentTranslation(key, new ChatComponentTranslation(item.getUnlocalizedName())),
                item);
        stat.registerStat();
        return stat;
    }
}
