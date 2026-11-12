package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mitchej123.hodgepodge.HodgepodgeEventHandler;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP_ReturnCursorStack {

    @WrapMethod(method = "closeContainer")
    private void hodgepodge$returnAndSyncInventory(Operation<Void> original) {
        if (!TweaksConfig.avoidDroppingItemsWhenClosing) {
            original.call();
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) (Object) this;
        HodgepodgeEventHandler.closingContainers.put(player, false);
        boolean synchronize;
        try {
            original.call();
        } finally {
            synchronize = Boolean.TRUE.equals(HodgepodgeEventHandler.closingContainers.remove(player));
        }
        if (synchronize) {
            player.sendContainerToPlayer(player.inventoryContainer);
        }
    }
}
