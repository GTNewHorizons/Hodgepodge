package com.mitchej123.hodgepodge.mixins.early.minecraft.villager;

import java.util.ArrayList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantRecipeList.class)
public class MixinMerchantRecipeList_WritePacketData extends ArrayList<MerchantRecipe> {

    @Inject(method = "func_151391_a", at = @At(value = "TAIL"))
    public void hodgepodge$writePacketData(PacketBuffer buffer, CallbackInfo ci) {
        for (MerchantRecipe recipe : this) {
            buffer.writeVarIntToBuffer(((AccessorMerchantRecipe) recipe).hodgepodge$getToolUses());
            buffer.writeVarIntToBuffer(((AccessorMerchantRecipe) recipe).hodgepodge$getMaxTradeUses());
        }
    }
}
