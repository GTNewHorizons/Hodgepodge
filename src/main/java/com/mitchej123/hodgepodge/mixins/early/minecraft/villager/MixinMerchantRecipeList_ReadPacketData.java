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
        // This is a S3FPacketCustomPayload, so the buffer is almost always larger than needed with the rest being zeros
        // To make this not set tool uses and max trade uses to zero if hodgepodge is not present on the server side,
        // cancel if the signal from the server side patch is not detected
        if (buffer.readableBytes() == 0 || !buffer.readBoolean()) {
            // we're on a vanilla server, so we don't know max trades, so like vanilla we don't limit trades at all
            for (Object recipe : original) {
                ((AccessorMerchantRecipe) recipe).hodgepodge$setMaxTradeUses(Integer.MAX_VALUE);
            }
            return original;
        }

        for (Object recipe : original) {
            ((AccessorMerchantRecipe) recipe).hodgepodge$setToolUses(buffer.readVarIntFromBuffer());
            ((AccessorMerchantRecipe) recipe).hodgepodge$setMaxTradeUses(buffer.readVarIntFromBuffer());
        }
        return original;
    }
}
