package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.IOException;

import net.minecraft.command.CommandException;
import net.minecraft.command.server.CommandSaveAll;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.util.WorldDataSaver;

@Mixin(CommandSaveAll.class)
public class MixinCommandSaveAll_WorldDataSave {

    @Inject(method = "processCommand", at = @At(value = "CONSTANT", args = "stringValue=commands.save.flushEnd"))
    private void hodgepodge$flushWorldData(CallbackInfo ci) {
        try {
            WorldDataSaver.INSTANCE.flush();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CommandException("commands.save.failed", "Interrupted while flushing world data");
        } catch (IOException e) {
            throw new CommandException("commands.save.failed", e.getMessage());
        }
    }
}
