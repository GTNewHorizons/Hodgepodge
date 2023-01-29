package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(value = ServerConfigurationManager.class, remap = false)
public class MixinServerConfigurationManager {

    /*
     * Make sure extra hearts aren't lost on dimension change Backported fix from
     * https://github.com/MinecraftForge/MinecraftForge/pull/4830
     */
    @Redirect(
            method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraft/world/Teleporter;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;firePlayerChangedDimensionEvent(Lnet/minecraft/entity/player/EntityPlayer;II)V"))
    private void hodgepodge$firePlayerChangedDimensionEvent(FMLCommonHandler instance, EntityPlayer player, int fromDim,
            int toDim) {
        if (player instanceof EntityPlayerMP) {
            ServersideAttributeMap attributeMap = (ServersideAttributeMap) player.getAttributeMap();
            @SuppressWarnings("unchecked")
            Collection<IAttributeInstance> watchedAttribs = attributeMap.getWatchedAttributes();
            if (!watchedAttribs.isEmpty()) {
                ((EntityPlayerMP) player).playerNetServerHandler
                        .sendPacket(new S20PacketEntityProperties(player.getEntityId(), watchedAttribs));
            }
        }

        // Fire the original redirected call
        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, fromDim, toDim);
    }
}
