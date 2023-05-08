package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.entities.golems.Marker;

@Mixin(ItemGolemBell.class)
public class MixinItemGolemBell {

    @Overwrite
    public static ArrayList<Marker> getMarkers(ItemStack stack) {
        ArrayList<Marker> markers = new ArrayList<>();
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("markers")) {
            NBTTagList tl = stack.stackTagCompound.getTagList("markers", 10);
            for (int i = 0; i < tl.tagCount(); i++) {
                NBTTagCompound nbttagcompound1 = tl.getCompoundTagAt(i);
                int x = nbttagcompound1.getInteger("x");
                int y = nbttagcompound1.getInteger("y");
                int z = nbttagcompound1.getInteger("z");
                int dim = nbttagcompound1.getInteger("dim");
                byte s = nbttagcompound1.getByte("side");
                byte c = nbttagcompound1.getByte("color");
                markers.add(new Marker(x, y, z, dim, s, c));
            }
        }
        return markers;
    }

}
