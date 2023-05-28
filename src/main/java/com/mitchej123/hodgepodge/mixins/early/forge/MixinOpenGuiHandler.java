package com.mitchej123.hodgepodge.mixins.early.forge;

import static org.objectweb.asm.Opcodes.PUTFIELD;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLMessage;
import cpw.mods.fml.common.network.internal.OpenGuiHandler;
import io.netty.channel.SimpleChannelInboundHandler;

@Mixin(value = { OpenGuiHandler.class })
public abstract class MixinOpenGuiHandler extends SimpleChannelInboundHandler<FMLMessage.OpenGui> {

    boolean openGuiSuccess = false;

    /*
     * Copy the logic from player.openGui to explicitly check if the getLocalGuiContainer failed
     */
    @Redirect(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/internal/FMLMessage$OpenGui;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;openGui(Ljava/lang/Object;ILnet/minecraft/world/World;III)V"),
            remap = false)
    public void hodgepodge$openGui(EntityPlayer player, Object mod, int modGuiId, World world, int x, int y, int z) {
        this.openGuiSuccess = false;
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(mod);
        Object guiContainer = NetworkRegistry.INSTANCE.getLocalGuiContainer(mc, player, modGuiId, world, x, y, z);
        if (guiContainer != null) {
            FMLCommonHandler.instance().showGuiScreen(guiContainer);
            this.openGuiSuccess = true;
        }
    }

    /*
     * if openGui failed, we return from channelRead0 early to avoid setting windowId on the wrong openContainer
     */
    @Inject(
            method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/internal/FMLMessage$OpenGui;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/inventory/Container;windowId:I", opcode = PUTFIELD),
            cancellable = true)
    public void hodgepodge$dontSetWindowId(CallbackInfo ci) {
        if (!this.openGuiSuccess) ci.cancel();
    }
}
