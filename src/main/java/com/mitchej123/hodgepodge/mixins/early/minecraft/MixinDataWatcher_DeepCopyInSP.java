package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.DataWatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DataWatcher.class)
public class MixinDataWatcher_DeepCopyInSP {

    @ModifyArg(
            method = { "getAllWatched", "getChanged" },
            at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", remap = false))
    private Object deepCopy(Object o) {
        final DataWatcher.WatchableObject obj = (DataWatcher.WatchableObject) o;
        switch (obj.getObjectType()) {
            case 0, 1, 3, 2, 4: // Byte, Short, Integer, Float
                return new DataWatcher.WatchableObject(obj.getObjectType(), obj.getDataValueId(), obj.getObject());
            case 5: // ItemStack - can be null
                final ItemStack itemstack = (ItemStack) obj.getObject();
                final ItemStack copy = itemstack == null ? null : itemstack.copy();
                return new DataWatcher.WatchableObject(obj.getObjectType(), obj.getDataValueId(), copy);
            case 6: // ChunkCoordinates
                final ChunkCoordinates coords = (ChunkCoordinates) obj.getObject();
                return new DataWatcher.WatchableObject(
                        obj.getObjectType(),
                        obj.getDataValueId(),
                        new ChunkCoordinates(coords));
        }
        return o;
    }
}
