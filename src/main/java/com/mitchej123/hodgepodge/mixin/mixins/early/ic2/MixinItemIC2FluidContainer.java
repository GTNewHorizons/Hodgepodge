package com.mitchej123.hodgepodge.mixin.mixins.early.ic2;

import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ic2.core.item.ItemIC2FluidContainer;

@Mixin(ItemIC2FluidContainer.class)
public class MixinItemIC2FluidContainer {

    @Redirect(
            method = "addInformation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidRegistry;getFluidName(Lnet/minecraftforge/fluids/FluidStack;)Ljava/lang/String;",
                    remap = false))
    private String hodgepodge$getFluidName(FluidStack fs) {
        return fs.getLocalizedName();
    }
}
