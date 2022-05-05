package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.Hodgepodge;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent.Serializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

@Mixin(S02PacketChat.class)
public abstract class MixinS02PacketChat {
    private static final Logger LOGGER = LogManager.getLogger("ChatOverflowFix");

    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeStringToBuffer(Ljava/lang/String;)V"))
    public void redirectSerialize(PacketBuffer instance, String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > 32767) {
            if (Hodgepodge.config.logHugeChat) {
                String incidentId = "" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000);
                LOGGER.info("HUGE chat message caught. Incident ID {}. Serialized message {}.", incidentId, s);
                bytes = Serializer.func_150696_a(new ChatComponentText(EnumChatFormatting.RED + "Someone tried to sent you a huge chat message that would kick you. Ask your server admin to see details. Please provide these info while reporting: Incident ID " + EnumChatFormatting.UNDERLINE + incidentId)).getBytes(StandardCharsets.UTF_8);
            } else {
                LOGGER.info("HUGE chat message caught. Details are not logged here as requested in config.");
                bytes = Serializer.func_150696_a(new ChatComponentText(EnumChatFormatting.RED + "Someone tried to sent you a huge chat message that would kick you. Ask your server admin to turn on logHugeChat in HodgePodge if this keeps happening.")).getBytes(StandardCharsets.UTF_8);
            }
        }
        instance.writeVarIntToBuffer(bytes.length);
        instance.writeBytes(bytes);
    }
}
