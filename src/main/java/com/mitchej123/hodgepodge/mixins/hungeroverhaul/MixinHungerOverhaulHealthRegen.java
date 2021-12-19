package com.mitchej123.hodgepodge.mixins.hungeroverhaul;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import iguanaman.hungeroverhaul.HungerOverhaul;
import iguanaman.hungeroverhaul.config.IguanaConfig;
import iguanaman.hungeroverhaul.food.FoodEventHandler;
import squeek.applecore.api.hunger.HealthRegenEvent;
import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FoodEventHandler.class, remap = false)
public class MixinHungerOverhaulHealthRegen {

    /**
     * @author mitchej123
     * @reason Fix bad interaction with SoL Carrot Edition
     */
    @Overwrite()
    @SubscribeEvent
    public void onHealthRegenTick(HealthRegenEvent.GetRegenTickPeriod event) {
        float wellfedModifier = 1.0F;
        if (event.player.isPotionActive(HungerOverhaul.potionWellFed))
            wellfedModifier = 0.75F;

        EnumDifficulty difficulty = event.player.worldObj.difficultySetting;
        float difficultyModifierHealing = 1.0F;
        if (IguanaConfig.difficultyScalingHealing) {
            if (difficulty.getDifficultyId() <= EnumDifficulty.EASY.getDifficultyId())
                difficultyModifierHealing = 0.75F;
            else if (difficulty == EnumDifficulty.HARD)
                difficultyModifierHealing = 1.5F;
        }

        float lowHealthModifier = 1.0F;
        if (IguanaConfig.modifyRegenRateOnLowHealth) {
            // Fix for SoL Carrot & Tinkers hearts, cap both hearts at 20 for calculation
            lowHealthModifier = Math.min(event.player.getMaxHealth(), 20) - Math.min(event.player.getHealth(), 20);
            lowHealthModifier *= IguanaConfig.lowHealthRegenRateModifier / 100F;
            lowHealthModifier *= difficultyModifierHealing;
            lowHealthModifier = (float) Math.pow(lowHealthModifier + 1F, 1.5F);
        }

        event.regenTickPeriod = Math.round(80.0F * difficultyModifierHealing * wellfedModifier * lowHealthModifier / (IguanaConfig.healthRegenRatePercentage / 100F));
    }
}

