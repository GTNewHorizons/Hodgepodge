package com.mitchej123.hodgepodge.mixins.early.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityWitch.class)
public abstract class MixinEntityWitch {

    @WrapOperation(
            method = "onLivingUpdate",
            at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item;II)Lnet/minecraft/item/ItemStack;"
            ))
    private ItemStack changeWitchDrinkingPotion(Item item, int stackSize, int meta, Operation<ItemStack> original) {
        switch (meta) {
            case 8237 -> meta = 8205; // Water Breathing
            case 16307 -> meta = 8195; // Fire Resistance
            case 16341 -> meta = 8197; // Instant Health
            case 16274 -> meta = 8194; // Speed
        }
        return new ItemStack(item, stackSize, meta);
    }

    @WrapOperation(
            method = "attackEntityWithRangedAttack",
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;I)Lnet/minecraft/entity/projectile/EntityPotion;"
            ))
    private EntityPotion changeThrownHarmingPotion(World world, EntityLivingBase witch, int meta, Operation<ItemStack> original) {
        return new EntityPotion(world, witch, 16396);
    }

    @WrapOperation(
            method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityPotion;setPotionDamage(I)V"
            ))
    private void changeThrownPotion(EntityPotion instance, int meta, Operation<ItemStack> original) {
        switch (meta) {
            case 32698 -> meta = 16394;
            case 32660 -> meta = 16388;
            case 32696 -> meta = 16392;
        }
        original.call(instance, meta);
    }
}

