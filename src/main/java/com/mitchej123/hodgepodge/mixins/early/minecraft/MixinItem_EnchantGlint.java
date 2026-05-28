package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mixin(Item.class)
public abstract class MixinItem_EnchantGlint {

    /**
     * @author koolkrafter5
     * @reason Prevent enchantment glint from rendering once per render pass. Only render it on the final pass for all
     *         items. Rendering it earlier causes it to be behind later passes. Potions are special and have it on the
     *         first pass so only the liquid has the glint (matches overwritten vanilla logic).
     */
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        final Item item = (Item) (Object) this;
        return stack != null && item.hasEffect(stack)
                && (item instanceof ItemPotion ? pass == 0 : pass == item.getRenderPasses(stack.getItemDamage()) - 1);
    }
}
