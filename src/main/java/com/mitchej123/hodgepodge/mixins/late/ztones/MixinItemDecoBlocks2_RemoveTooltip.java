package com.mitchej123.hodgepodge.mixins.late.ztones;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.riciJak.Ztones.item.block.ItemDecoBlocks2;

@Mixin(ItemDecoBlocks2.class)
public class MixinItemDecoBlocks2_RemoveTooltip {

    @Inject(method = "addInformation", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$removeTooltip(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced,
            CallbackInfo ci) {
        ci.cancel();
    }
}
