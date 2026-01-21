package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.nio.charset.Charset;

import net.minecraft.server.management.UserList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.io.Files;

@SuppressWarnings("UnstableApiUsage")
@Mixin(UserList.class)
public class MixinUserList {

    @Redirect(
            method = "func_152679_g()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/io/Files;newReader(Ljava/io/File;Ljava/nio/charset/Charset;)Ljava/io/BufferedReader;",
                    remap = false))
    public BufferedReader redirectNewReader(File file, Charset charset) throws FileNotFoundException {
        if (file.exists()) {
            return Files.newReader(file, charset);
        } else {
            return new BufferedReader(new StringReader("[]"));
        }
    }
}
