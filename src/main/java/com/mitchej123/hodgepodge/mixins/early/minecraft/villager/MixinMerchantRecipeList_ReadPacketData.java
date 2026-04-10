package com.mitchej123.hodgepodge.mixins.early.minecraft.villager;

import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipeList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

@Mixin(MerchantRecipeList.class)
public class MixinMerchantRecipeList_ReadPacketData {

    @ModifyReturnValue(method = "func_151390_b", at = @At(value = "TAIL"))
    private static MerchantRecipeList hodgepodge$readPacketData(MerchantRecipeList original, PacketBuffer buffer) {
        for (Object recipe : original) {
            ((AccessorMerchantRecipe) recipe).hodgepodge$setToolUses(buffer.readVarIntFromBuffer());
            ((AccessorMerchantRecipe) recipe).hodgepodge$setMaxTradeUses(buffer.readVarIntFromBuffer());
        }
        return original;
    }
}
