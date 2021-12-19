package com.mitchej123.hodgepodge.mixins.railcraft;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

import com.mitchej123.hodgepodge.core.util.AnchorAlarm;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorPersonal;
import mods.railcraft.common.blocks.machine.alpha.TileAnchorWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileAnchorPersonal.class)
public class MixinTileAnchorPersonal extends TileAnchorWorld {
    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        AnchorAlarm.addNewAnchor(entityliving, this);
    }
}
