package com.mitchej123.hodgepodge.mixins.late.extrautilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.block.BlockDrum;

@Mixin(BlockDrum.class)
abstract public class MixinBlockDrum {

    @Redirect(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;hasContainerItem(Lnet/minecraft/item/ItemStack;)Z",
                    remap = false))
    private boolean hodgepodge$redirectHasContainerItem(Item item, ItemStack itemStack) {
        return true;
    }

    @Redirect(
            method = "onBlockActivated",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;getContainerItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;",
                    remap = false))
    private ItemStack hodgepodge$redirectGetContainerItem(Item item, ItemStack itemStack) {
        return FluidContainerRegistry.drainFluidContainer(itemStack);
    }
}
