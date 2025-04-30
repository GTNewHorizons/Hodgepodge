package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.registry.GameRegistry;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEndermanPlaceBlacklist extends EntityMob {

    @Unique
    private static final ArrayList<ItemStack> hodgepodge$blacklist = new ArrayList<>();
    static {
        for (String s : TweaksConfig.endermanBlockPlaceBlacklistBlocks) {
            ItemStack stack = hodgepodge$parseConfigLine(s);
            if (stack != null) {
                hodgepodge$blacklist.add(stack);
            }
        }
    }

    public MixinEntityEndermanPlaceBlacklist(World p_i1738_1_) {
        super(p_i1738_1_);
    }

    @WrapOperation(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;renderAsNormalBlock()Z"))
    private boolean checkEndermanBlockPlace(Block instance, Operation<Boolean> original, @Local(ordinal = 0) int k,
            @Local(ordinal = 1) int i, @Local(ordinal = 2) int j) {
        Item item = Item.getItemFromBlock(instance);
        int meta = worldObj.getBlockMetadata(k, i - 1, j);
        if (hodgepodge$isBlockInEndermanPlaceBlacklist(item, meta)) {
            return false;
        }
        return original.call(instance);
    }

    @Unique
    private boolean hodgepodge$isBlockInEndermanPlaceBlacklist(Item item, int meta) {
        for (ItemStack is : hodgepodge$blacklist) {
            if (Objects.equals(is.getItem(), item)
                    && (is.getItemDamage() == OreDictionary.WILDCARD_VALUE || is.getItemDamage() == meta)) {
                return true;
            }
        }

        return false;
    }

    @Unique
    private static ItemStack hodgepodge$parseConfigLine(String str) {
        String[] input = str.split(":");
        if (input.length < 2) {
            Common.log.warn(
                    "Unable to parse item stack \"{}\" for enderman blacklist. Valid format is \"modId:name(:meta optional)\"",
                    str);
            return null;
        }
        ItemStack itemStack = GameRegistry.findItemStack(input[0], input[1], 1);
        if (itemStack == null) {
            Common.log.warn("Unable to find item stack \"{}\" for enderman blacklist", str);
            return null;
        }
        if (input.length < 3) {
            return itemStack;
        }
        try {
            itemStack.setItemDamage(Integer.parseInt(input[2]));
        } catch (NumberFormatException e) {
            Common.log.warn(
                    "Invalid metadata value {} for item stack {}:{} for enderman blacklist",
                    input[2],
                    input[0],
                    input[1]);
        }
        return itemStack;
    }
}
