package com.mitchej123.hodgepodge.mixins.late.ztones;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.riciJak.Ztones.item.block.ItemDecoBlocks2;

@Mixin(ItemDecoBlocks2.class)
public class MixinItemDecoBlocks2_RemoveTooltip {

    /**
     * @author Armisael
     * @reason Remove the "Alt/Ctrl+Scroll to Cycle Between Blocks" tooltip, which is wrong once scroll swapping is
     *         disabled or uses a rebindable key.
     */
    @Overwrite
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {}
}
