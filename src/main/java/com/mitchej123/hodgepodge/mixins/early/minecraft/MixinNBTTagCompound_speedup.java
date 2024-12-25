package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NBTTagCompound.class)
public class MixinNBTTagCompound_speedup {
    @Shadow
    private Map<String, NBTBase> tagMap; // = new Object2ObjectOpenHashMap<>(); via ASM

    /**
     * @author mitchej123
     * @reason Speedup copy by using an Object2ObjectOpenHashMap FastEntrySet iterator instead of a key iterator and a lookup
     *         Requires the ASM transformer to have run and swap the HashMap construction with a Object2ObjectOpenHashMap
     */
    @Overwrite
    public NBTBase copy() {

        NBTTagCompound nbttagcompound = new NBTTagCompound();
        final Object2ObjectOpenHashMap.FastEntrySet<String, NBTBase> entries = ((Object2ObjectOpenHashMap<String, NBTBase>) tagMap).object2ObjectEntrySet();
        final ObjectIterator<Object2ObjectMap.Entry<String, NBTBase>> fastIterator = entries.fastIterator();
        while (fastIterator.hasNext()) {
            final Object2ObjectMap.Entry<String, NBTBase> entry = fastIterator.next();
            nbttagcompound.setTag(entry.getKey(), entry.getValue().copy());
        }
        return nbttagcompound;
    }
}
