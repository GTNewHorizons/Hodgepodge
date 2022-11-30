package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = NetHandlerPlayClient.class)
public class MixinNetHandlePlayClient {

    private static final int randomChannel = 43284;

    @Shadow
    private Minecraft gameController;

    /**
     * @author Quarri6343
     * @reason Stop "You can only sleep at night" message filling the chat
     */
    @Overwrite
    public void handleChat(S02PacketChat packetIn) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(packetIn.func_148915_c());
        if (!MinecraftForge.EVENT_BUS.post(event) && event.message != null) {
            if (event.message.equals(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]))
                    || event.message.equals(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]))
                    || event.message.equals(new ChatComponentTranslation("tile.bed.occupied", new Object[0]))) {
                this.gameController
                        .ingameGUI
                        .getChatGUI()
                        .printChatMessageWithOptionalDeletion(event.message, randomChannel);
            } else {
                this.gameController.ingameGUI.getChatGUI().printChatMessage(event.message);
            }
        }
    }
}
