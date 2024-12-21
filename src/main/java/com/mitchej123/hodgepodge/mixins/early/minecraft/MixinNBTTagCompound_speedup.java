package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NBTTagCompound.class)
public class MixinNBTTagCompound_speedup {

    @Shadow
    private Map<String, NBTBase> tagMap;

    /**
     * @author mitchej123
     * @reason Speedup copy by using an entrySet iterator instead of a key iterator and a lookup
     */
    @Overwrite
    public NBTBase copy() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        for (Map.Entry<String, NBTBase> entry : tagMap.entrySet()) {
            nbttagcompound.setTag(entry.getKey(), entry.getValue().copy());
        }
        return nbttagcompound;
    }
}
