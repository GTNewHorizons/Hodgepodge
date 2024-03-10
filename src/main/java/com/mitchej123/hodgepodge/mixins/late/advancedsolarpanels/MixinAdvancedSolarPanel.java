package com.mitchej123.hodgepodge.mixins.late.advancedsolarpanels;

import net.minecraft.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import advsolar.common.AdvancedSolarPanel;

@Mixin(value = AdvancedSolarPanel.class, remap = false)
public class MixinAdvancedSolarPanel {

    @ModifyExpressionValue(
            at = @At(
                    ordinal = 0,
                    target = "Lic2/api/item/IC2Items;getItem(Ljava/lang/String;)Lnet/minecraft/item/ItemStack;",
                    value = "INVOKE"),
            method = "afterModsLoaded",
            slice = @Slice(
                    from = @At(
                            opcode = Opcodes.GETSTATIC,
                            target = "Ladvsolar/common/AdvancedSolarPanel;disableMolecularTransformerRecipe:Z",
                            value = "FIELD")))
    private ItemStack hodgepodge$fixDamage(ItemStack original) {
        // Must be a new ItemStack so the field in Ic2Items is not modified
        return new ItemStack(original.getItem(), 1, 1);
    }

}
