package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.inventory.Container;

import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.internal.FMLMessage;
import cpw.mods.fml.common.network.internal.FMLMessage.OpenGui;
import cpw.mods.fml.common.network.internal.OpenGuiHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Mixin(OpenGuiHandler.class)
public abstract class MixinOpenGuiHandler extends SimpleChannelInboundHandler<FMLMessage.OpenGui> {

    @Shadow(remap = false)
    protected abstract void channelRead0(ChannelHandlerContext ctx, OpenGui msg) throws Exception;

    @Intrinsic(displace = true)
    protected void hodgepodge$channelRead0(ChannelHandlerContext ctx, OpenGui msg) throws Exception {
        Container playerContainer = FMLClientHandler.instance().getClient().thePlayer.openContainer;
        int playerContainerId = playerContainer.windowId;
        this.channelRead0(ctx, msg);
        if (playerContainer == FMLClientHandler.instance().getClient().thePlayer.openContainer)
            playerContainer.windowId = playerContainerId;
    }
}
