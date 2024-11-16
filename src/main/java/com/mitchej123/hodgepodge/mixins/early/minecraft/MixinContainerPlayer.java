package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerPlayer.class)
public abstract class MixinContainerPlayer extends Container {

    @Shadow
    public IInventory craftResult;

    // inject point relies on a bukkit patch:
    // https://github.com/GTNewHorizons/Thermos/blob/ab8a329b10f0857cb246e5d791ede8e1f7419c63/patches/net/minecraft/inventory/ContainerPlayer.java.patch#L58
    @Inject(
            method = "onCraftMatrixChanged",
            at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"),
            cancellable = true)
    private void hodgepodge$bukkitCraftMatrixChangedProxy(CallbackInfo ci) {
        if (crafters.get(0) instanceof EntityPlayerMP playerMP) {
            ItemStack result = craftResult.getStackInSlot(0);
            playerMP.playerNetServerHandler
                    .sendPacket(new S2FPacketSetSlot(playerMP.openContainer.windowId, 0, result));
        }
        ci.cancel();
    }
}
