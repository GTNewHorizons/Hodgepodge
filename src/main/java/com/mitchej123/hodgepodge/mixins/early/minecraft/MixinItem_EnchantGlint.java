package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(Item.class)
public abstract class MixinItem_EnchantGlint {

    /**
     * @author koolkrafter5
     * @reason Prevent enchantment glint from rendering once per render pass. Only render it on the final pass for all
     *         items. Potions are special and have it on the first pass so only the liquid has the glint.
     */
    @Inject(method = "hasEffect(Lnet/minecraft/item/ItemStack;I)Z", at = @At("HEAD"), cancellable = true, remap = false)
    @SideOnly(Side.CLIENT)
    private void fixGlint(ItemStack stack, int pass, CallbackInfoReturnable<Boolean> cir) {
        Item item = (Item) (Object) this;
        cir.setReturnValue(
                stack != null && item.hasEffect(stack)
                        && (item instanceof ItemPotion ? pass == 0
                                : pass == item.getRenderPasses(stack.getItemDamage()) - 1));
    }
}
