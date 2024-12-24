package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;

@Mixin(MapStorage.class)
public class MixinMapStorage_threadedIO {

    @Shadow
    private ISaveHandler saveHandler;

    /**
     * @author mitchej123
     * @reason Async saving
     */
    @Overwrite
    private void saveData(WorldSavedData data) {
        if (this.saveHandler == null) return;

        if (!TweaksConfig.saveMineshaftData && data.mapName.equals("Mineshaft")) return;

        File file = this.saveHandler.getMapFileFromName(data.mapName);
        if (file == null) return;

        NBTTagCompound tag = new NBTTagCompound();
        data.writeToNBT(tag);
        NBTTagCompound parentTag = new NBTTagCompound();
        parentTag.setTag("data", tag);

        HodgepodgeCore.saveWorldData(file, parentTag);

    }

}
