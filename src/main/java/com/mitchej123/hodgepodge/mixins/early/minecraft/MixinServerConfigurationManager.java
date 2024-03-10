package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collection;

import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.Teleporter;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerConfigurationManager.class, remap = false)
public class MixinServerConfigurationManager {

    /*
     * Make sure extra hearts aren't lost on dimension change Backported fix from
     * https://github.com/MinecraftForge/MinecraftForge/pull/4830
     */
    @Inject(
            at = @At(
                    target = "Lcpw/mods/fml/common/FMLCommonHandler;instance()Lcpw/mods/fml/common/FMLCommonHandler;",
                    value = "INVOKE"),
            method = "transferPlayerToDimension(Lnet/minecraft/entity/player/EntityPlayerMP;ILnet/minecraft/world/Teleporter;)V")
    private void hodgepodge$sendEntityProperties(EntityPlayerMP player, int dimension, Teleporter teleporter,
            CallbackInfo ci) {
        ServersideAttributeMap attributeMap = (ServersideAttributeMap) player.getAttributeMap();
        @SuppressWarnings("unchecked")
        Collection<IAttributeInstance> watchedAttribs = attributeMap.getWatchedAttributes();
        if (!watchedAttribs.isEmpty()) {
            player.playerNetServerHandler
                    .sendPacket(new S20PacketEntityProperties(player.getEntityId(), watchedAttribs));
        }
    }
}
