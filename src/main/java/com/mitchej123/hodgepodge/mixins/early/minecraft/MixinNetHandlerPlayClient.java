package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlePlayClient {

    @Unique
    private static final int randomChannel = 43284;

    @Shadow
    private Minecraft gameController;

    /**
     * @author Quarri6343
     * @reason Stop "You can only sleep at night" message filling the chat
     */
    @Inject(
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/network/NetHandlerPlayClient;gameController:Lnet/minecraft/client/Minecraft;",
                    value = "FIELD"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT,
            method = "handleChat")
    private void hodgepodge$handleBedMessage(S02PacketChat packetIn, CallbackInfo ci, ClientChatReceivedEvent event) {
        if (event.message.equals(new ChatComponentTranslation("tile.bed.noSleep"))
                || event.message.equals(new ChatComponentTranslation("tile.bed.notSafe"))
                || event.message.equals(new ChatComponentTranslation("tile.bed.occupied"))) {
            this.gameController.ingameGUI.getChatGUI()
                    .printChatMessageWithOptionalDeletion(event.message, RANDOM_CHANNEL);
            ci.cancel();
        }
    }
}
