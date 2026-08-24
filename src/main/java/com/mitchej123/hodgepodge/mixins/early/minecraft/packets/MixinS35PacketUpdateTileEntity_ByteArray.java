package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import java.io.IOException;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.config.SpeedupsConfig;

@Mixin(S35PacketUpdateTileEntity.class)
public class MixinS35PacketUpdateTileEntity_ByteArray {

    @Redirect(
            method = "readPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketBuffer;readNBTTagCompoundFromBuffer()Lnet/minecraft/nbt/NBTTagCompound;"))
    public NBTTagCompound hodgepodge$readNBTTagCompoundFromBuffer(PacketBuffer data) throws IOException {
        if (!SpeedupsConfig.skipTileEntityNbtSerializationCode) {
            return data.readNBTTagCompoundFromBuffer();
        }
        byte id = data.readByte();
        if (id == 0) {
            return data.readNBTTagCompoundFromBuffer();
        }
        String key = String.valueOf((char) id);
        byte[] buffer = new byte[data.readVarIntFromBuffer()];
        data.readBytes(buffer);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByteArray(key, buffer);
        return tag;
    }

    @Redirect(
            method = "writePacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketBuffer;writeNBTTagCompoundToBuffer(Lnet/minecraft/nbt/NBTTagCompound;)V"))
    public void hodgepodge$writeNBTTagCompoundFromBuffer(PacketBuffer data, NBTTagCompound nbt) throws IOException {
        if (!SpeedupsConfig.skipTileEntityNbtSerializationCode) {
            data.writeNBTTagCompoundToBuffer(nbt);
            return;
        }
        Set<String> keys = nbt.func_150296_c();
        if (keys.size() != 1) {
            data.writeByte(0);
            data.writeNBTTagCompoundToBuffer(nbt);
            return;
        }
        String key = keys.iterator().next();
        if (key.length() != 1) {
            data.writeByte(0);
            data.writeNBTTagCompoundToBuffer(nbt);
            return;
        }
        char k = key.charAt(0);
        if (k == 0 || k > 255) {
            data.writeByte(0);
            data.writeNBTTagCompoundToBuffer(nbt);
            return;
        }
        if (!nbt.hasKey(key, Constants.NBT.TAG_BYTE_ARRAY)) {
            data.writeByte(0);
            data.writeNBTTagCompoundToBuffer(nbt);
            return;
        }
        byte[] arr = nbt.getByteArray(key);
        data.writeByte(k);
        data.writeVarIntToBuffer(arr.length);
        data.writeBytes(arr);
    }
}
