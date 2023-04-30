package com.mitchej123.hodgepodge.mixins.late.railcraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.util.AnchorAlarm;

import mods.railcraft.common.blocks.machine.alpha.TileAnchorPersonal;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;

@Mixin(TileAnchorPersonal.class)
public class MixinTileAnchorPersonal extends TileAnchorWorld {

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        AnchorAlarm.addNewAnchor(entityliving, this);
    }
}
