package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.monster.EntityWitch;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityWitch.class)
public abstract class MixinEntityWitch {

    @ModifyArg(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;II)V"),
            index = 2)
    private int changeWitchDrinkingPotion(int meta) {
        return switch (meta) {
            case 8237 -> 8205; // Water Breathing
            case 16307 -> 8195; // Fire Resistance
            case 16341 -> 8197; // Instant Health
            case 16274 -> 8194; // Speed
            default -> meta;
        };
    }

    @ModifyArg(
            method = "attackEntityWithRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/EntityPotion;<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;I)V"))
    private int changeThrownHarmingPotion(int meta) {
        return 16396;
    }

    @ModifyArg(
            method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityPotion;setPotionDamage(I)V"))
    private int changeThrownPotion(int meta) {
        return switch (meta) {
            case 32698 -> 16394; // Slowness
            case 32660 -> 16388; // Poison
            case 32696 -> 16392; // Weakness
            default -> meta;
        };
    }
}
