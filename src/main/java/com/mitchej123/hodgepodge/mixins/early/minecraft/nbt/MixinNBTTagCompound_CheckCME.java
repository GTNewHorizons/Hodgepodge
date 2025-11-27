package com.mitchej123.hodgepodge.mixins.early.minecraft.nbt;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.NBTTagCompoundConcurrentModificationException;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound_CheckCME {

    @Shadow
    protected static void func_150298_a(String name, NBTBase data, DataOutput output) throws IOException {}

    @Shadow
    public abstract NBTBase getTag(String key);

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object hodgepodge$checkCME(Iterator<?> instance) {
        if ("File IO Thread".equals(Thread.currentThread().getName())) {
            // only do this on chunk save thread
            try {
                return instance.next();
            } catch (ConcurrentModificationException ex) {
                throw new NBTTagCompoundConcurrentModificationException(ex, this);
            }
        } else {
            return instance.next();
        }
    }

    @Redirect(
            method = "write",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;func_150298_a(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;Ljava/io/DataOutput;)V"))
    private void hodgepodge$appendKeyPath(String name, NBTBase data, DataOutput output) throws IOException {
        if ("File IO Thread".equals(Thread.currentThread().getName())) {
            // only do this on chunk save thread
            try {
                func_150298_a(name, data, output);
            } catch (NBTTagCompoundConcurrentModificationException ex) {
                String prefix = Stream.of("id", "mID", "x", "y", "z").map(s -> s + "=" + this.getTag(s))
                        .collect(Collectors.joining(",", "[", "]"));
                if (prefix.length() > 2) { // len(prefix + suffix) == 2
                    ex.addKeyPath(String.format("[%s]%s", prefix, name));
                } else {
                    ex.addKeyPath(name);
                }
                ex.setFullTag(this);
                throw ex;
            }
        } else {
            func_150298_a(name, data, output);
        }
    }
}
