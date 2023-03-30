package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.util.NBTTagCompoundConcurrentModificationException;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound {

    @Shadow
    protected static void func_150298_a(String name, NBTBase data, DataOutput output) throws IOException {}

    @Shadow
    public abstract String getString(String key);

    @Shadow
    public abstract int getInteger(String key);

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
                NBTTagCompound thiz = (NBTTagCompound) ((Object) this);
                StringBuilder prefix = new StringBuilder();
                if (thiz.hasKey("id", Constants.NBT.TAG_STRING)) {
                    prefix.append("id=").append(this.getString("id"));
                }
                if (thiz.hasKey("mID", Constants.NBT.TAG_INT)) {
                    prefix.append("mID=").append(this.getInteger("mID"));
                }
                if (prefix.length() > 0) {
                    ex.addKeyPath(String.format("[%s]%s", prefix, name));
                } else {
                    ex.addKeyPath(name);
                }
                throw ex;
            }
        } else {
            func_150298_a(name, data, output);
        }
    }
}
