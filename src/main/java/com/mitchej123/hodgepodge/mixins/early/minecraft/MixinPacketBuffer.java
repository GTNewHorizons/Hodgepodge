package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.Common;

@Mixin(PacketBuffer.class)
public class MixinPacketBuffer {

    // A magic value that means the real length of the packet is encoded in a following int.
    @Unique
    private static final short MAGIC_LENGTH_IS_INT = Short.MAX_VALUE;

    /**
     * @author eigenraven
     * @reason Change the length encoding to support values longer than a short, requires changing of local variable
     *         types.
     */
    @Overwrite
    public NBTTagCompound readNBTTagCompoundFromBuffer() throws IOException {
        final PacketBuffer self = (PacketBuffer) (Object) this;
        short shortLength = self.readShort();
        final int realLength;
        if (shortLength < 0) {
            return null;
        } else if (shortLength == MAGIC_LENGTH_IS_INT) {
            realLength = self.readInt();
        } else {
            realLength = shortLength;
        }
        byte[] buffer = new byte[realLength];
        self.readBytes(buffer);
        return CompressedStreamTools.func_152457_a(buffer, new NBTSizeTracker(Common.config.maxNetworkNbtSizeLimit));
    }

    /**
     * @author eigenraven
     * @reason Change the length encoding to support values longer than a short, requires changing of local variable
     *         types.
     */
    @Overwrite
    public void writeNBTTagCompoundToBuffer(NBTTagCompound nbt) throws IOException {
        final PacketBuffer self = (PacketBuffer) (Object) this;
        if (nbt == null) {
            self.writeShort(-1);
        } else {
            byte[] buffer = CompressedStreamTools.compress(nbt);
            if (buffer.length >= MAGIC_LENGTH_IS_INT) {
                self.writeShort(MAGIC_LENGTH_IS_INT);
                self.writeInt(buffer.length);
            } else {
                self.writeShort(buffer.length);
            }
            self.writeBytes(buffer);
        }
    }

}
