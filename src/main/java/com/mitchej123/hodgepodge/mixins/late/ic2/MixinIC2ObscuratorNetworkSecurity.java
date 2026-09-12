package com.mitchej123.hodgepodge.mixins.late.ic2;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.registry.GameData;
import ic2.core.item.tool.ItemObscurator;

@Mixin(ItemObscurator.class)
public class MixinIC2ObscuratorNetworkSecurity {

    @Inject(method = "onPlayerItemNetworkData", at = @At("HEAD"), cancellable = true, remap = false)
    private void hodgepodge$validateObscuratorData(EntityPlayer player, int slot, Object[] data,
            CallbackInfo callback) {
        if (data.length != 3 || !(data[0] instanceof String)
                || !(data[1] instanceof Integer)
                || !(data[2] instanceof Integer)) {
            callback.cancel();
            return;
        }

        int metadata = (Integer) data[1];
        int side = (Integer) data[2];
        Block block = GameData.getBlockRegistry().getRaw((String) data[0]);
        if (block == null || metadata < 0 || metadata > 15 || side < 0 || side > 5) {
            callback.cancel();
        }
    }
}
