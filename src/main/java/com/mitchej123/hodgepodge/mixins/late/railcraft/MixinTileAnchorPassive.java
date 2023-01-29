package com.mitchej123.hodgepodge.mixins.late.railcraft;

import mods.railcraft.common.blocks.machine.alpha.TileAnchorPassive;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.util.AnchorAlarm;

@Mixin(TileAnchorPassive.class)
public class MixinTileAnchorPassive extends TileAnchorWorld {

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        AnchorAlarm.addNewAnchor(entityliving, this);
    }
}
