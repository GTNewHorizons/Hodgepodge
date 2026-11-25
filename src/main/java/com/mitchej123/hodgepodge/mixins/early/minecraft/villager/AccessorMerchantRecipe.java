package com.mitchej123.hodgepodge.mixins.early.minecraft.villager;

import net.minecraft.village.MerchantRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantRecipe.class)
public interface AccessorMerchantRecipe {

    @Accessor(value = "toolUses")
    int hodgepodge$getToolUses();

    @Accessor(value = "toolUses")
    void hodgepodge$setToolUses(int toolUses);

    @Accessor(value = "maxTradeUses")
    int hodgepodge$getMaxTradeUses();

    @Accessor(value = "maxTradeUses")
    void hodgepodge$setMaxTradeUses(int maxTradeUses);
}
