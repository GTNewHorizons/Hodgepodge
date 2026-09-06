package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(S02PacketChat.class)
public abstract class MixinS02PacketChat_FixHugeChatKick {

    @Unique
    private static final Logger LOGGER = LogManager.getLogger("ChatOverflowFix");

    /**
     * chatComponent
     */
    @Shadow
    private IChatComponent field_148919_a;

    @Redirect(
            method = "writePacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketBuffer;writeStringToBuffer(Ljava/lang/String;)V"))
    public void hodgepodge$redirectSerialize(PacketBuffer instance, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 32767) {
            String messageStart = field_148919_a.getUnformattedText().substring(0, 30);
            if (FixesConfig.logHugeChat) {
                String incidentId = "" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000);
                LOGGER.info("HUGE chat message caught. Incident ID {}. Serialized message {}.", incidentId, s);
                bytes = Serializer.func_150696_a /* componentToJson */ (
                        new ChatComponentTranslation(
                                "hodgepodge.chat.huge_message.logged",
                                // Formatting codes do not carry over into an argument, it is a component of its own
                                EnumChatFormatting.RED.toString() + EnumChatFormatting.UNDERLINE + incidentId,
                                messageStart))
                        .getBytes(StandardCharsets.UTF_8);
            } else {
                LOGGER.info("HUGE chat message caught. Details are not logged here as requested in config.");
                bytes = Serializer
                        .func_150696_a /* componentToJson */ (
                                new ChatComponentTranslation("hodgepodge.chat.huge_message.unlogged", messageStart))
                        .getBytes(StandardCharsets.UTF_8);
            }
        }
        instance.writeVarIntToBuffer(bytes.length);
        instance.writeBytes(bytes);
    }
}
