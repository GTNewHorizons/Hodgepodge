package com.mitchej123.hodgepodge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.command.CommandException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinCommandSaveAll_WorldDataSave;

class CommandSaveFlushTest {

    @TempDir
    Path temporary;

    @Test
    void commandFlushWaitsForRealIoAndReportsFailure() throws Exception {
        Method flush = MixinCommandSaveAll_WorldDataSave.class
                .getDeclaredMethod("hodgepodge$flushWorldData", CallbackInfo.class);
        flush.setAccessible(true);
        Object command = new MixinCommandSaveAll_WorldDataSave();
        NBTTagCompound data = new NBTTagCompound();
        data.setString("value", "saved");
        Path file = temporary.resolve("data.dat");
        try {
            WorldDataSaver.INSTANCE.saveData(file.toFile(), data, false, false);
            flush.invoke(command, new Object[] { null });
            assertEquals("saved", CompressedStreamTools.read(file.toFile()).getString("value"));
            Path blocked = Files.createDirectory(temporary.resolve("blocked"));
            Files.write(blocked.resolve("keep"), new byte[] { 1 });
            WorldDataSaver.INSTANCE.saveData(blocked.toFile(), data, false, false);
            InvocationTargetException failure = assertThrows(
                    InvocationTargetException.class,
                    () -> flush.invoke(command, new Object[] { null }));
            assertInstanceOf(CommandException.class, failure.getCause());
        } finally {
            WorldDataSaver.INSTANCE.closeSession();
        }
    }
}
