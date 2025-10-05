package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.S0EPacketSpawnObjectExt;

@Mixin(S0EPacketSpawnObject.class)
public class MixinS0EPacketSpawnObject_ItemThrower implements S0EPacketSpawnObjectExt {

    @Unique
    private String hodgepodge$itemThrower = null;
    @Unique
    private int hodgepodge$pickupDelay;

    @Inject(method = "<init>(Lnet/minecraft/entity/Entity;II)V", at = @At("TAIL"))
    private void hodgepodge$init(Entity entity, int p_i45166_2_, int p_i45166_3_, CallbackInfo ci) {
        if (entity instanceof EntityItem entityItem) {
            if (entityItem.func_145800_j() != null) {
                hodgepodge$itemThrower = entityItem.func_145800_j();
            }
            hodgepodge$pickupDelay = entityItem.delayBeforeCanPickup;
        }
    }

    @Inject(method = "writePacketData", at = @At("TAIL"))
    private void hodgepodge$write(PacketBuffer data, CallbackInfo ci) throws IOException {
        data.writeStringToBuffer(hodgepodge$itemThrower == null ? "" : hodgepodge$itemThrower);
        data.writeInt(hodgepodge$pickupDelay);
    }

    @Inject(method = "readPacketData", at = @At("TAIL"))
    private void hodgepodge$read(PacketBuffer data, CallbackInfo ci) throws IOException {
        if (data.readableBytes() >= 5) {
            final String name = data.readStringFromBuffer(16);
            hodgepodge$itemThrower = name.isEmpty() ? null : name;
            hodgepodge$pickupDelay = data.readInt();
        }
    }

    @Override
    public String hodgepodge$getThrower() {
        return hodgepodge$itemThrower;
    }

    @Override
    public int hodgepodge$getDelay() {
        return hodgepodge$pickupDelay;
    }
}
