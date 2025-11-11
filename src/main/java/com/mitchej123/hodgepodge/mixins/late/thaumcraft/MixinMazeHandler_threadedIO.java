package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixins.hooks.WorldDataSaverHook;

import thaumcraft.common.lib.world.dim.MazeHandler;

@Mixin(MazeHandler.class)
public class MixinMazeHandler_threadedIO {

    @Shadow(remap = false)
    private static NBTTagCompound writeNBT() {
        throw new UnsupportedOperationException("Mixin failed to shadow writeNBT");
    }

    /**
     * @author mitchej123
     * @reason Do the actual saving of the maze in the IO thread
     */
    @Overwrite(remap = false)
    public static void saveMaze(World world) {
        NBTTagCompound tag = writeNBT();
        NBTTagCompound parentTag = new NBTTagCompound();
        parentTag.setTag("data", tag);
        final String filename;

        // Adds support for Salis Arcana updating the labyrinth file format
        if (tag.hasKey("version")) {
            filename = "labyrinth_v" + tag.getInteger("version") + ".dat";
        } else {
            filename = "labyrinth.dat";
        }

        final File file = new File(world.getSaveHandler().getWorldDirectory(), filename);

        WorldDataSaverHook.saveWorldDataBackup(file, parentTag);

    }
}
