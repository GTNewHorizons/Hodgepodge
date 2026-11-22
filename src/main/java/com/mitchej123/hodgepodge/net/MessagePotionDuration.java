package com.mitchej123.hodgepodge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.mitchej123.hodgepodge.mixins.early.minecraft.PotionEffectAccessor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Carries the real duration of a potion effect, which S1DPacketEntityEffect truncates to a short (32767 ticks, about 27
 * minutes). Sent right after the vanilla packet, so it always arrives after the effect it corrects.
 */
public class MessagePotionDuration implements IMessage, IMessageHandler<MessagePotionDuration, IMessage> {

    public int entityId;
    public int potionId;
    public int duration;

    public MessagePotionDuration(int entityId, int potionId, int duration) {
        this.entityId = entityId;
        this.potionId = potionId;
        this.duration = duration;
    }

    public MessagePotionDuration() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.potionId = buf.readInt();
        this.duration = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.potionId);
        buf.writeInt(this.duration);
    }

    @Override
    public IMessage onMessage(MessagePotionDuration message, MessageContext ctx) {
        if (Minecraft.getMinecraft().theWorld == null) return null;
        if (message.potionId < 0 || message.potionId >= Potion.potionTypes.length) return null;
        final Potion potion = Potion.potionTypes[message.potionId];
        if (potion == null) return null;

        final Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityId);
        if (!(entity instanceof EntityLivingBase)) return null;

        final PotionEffect effect = ((EntityLivingBase) entity).getActivePotionEffect(potion);
        if (effect == null) return null;

        ((PotionEffectAccessor) effect).setDuration(message.duration);
        effect.setPotionDurationMax(false);

        return null;
    }
}
