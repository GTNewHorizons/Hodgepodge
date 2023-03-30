package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.DataOutput;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.util.NBTTagCompoundConcurrentModificationException;

@Mixin(NBTTagCompound.class)
public class MixinNBTTagCompound {

    @WrapOperation(
            method = "write",
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object hodgepodge$checkCME(Iterator<?> thiz, Operation<Object> original) {
        if ("File IO Thread".equals(Thread.currentThread().getName())) {
            // only do this on chunk save thread
            try {
                return original.call(thiz);
            } catch (ConcurrentModificationException ex) {
                throw new NBTTagCompoundConcurrentModificationException(ex, this);
            }
        } else {
            return original.call(thiz);
        }
    }

    @WrapOperation(
            method = "write",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;func_150298_a(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;Ljava/io/DataOutput;)V"))
    private void hodgepodge$appendKeyPath(String key, NBTBase value, DataOutput out, Operation<Void> original) {
        if ("File IO Thread".equals(Thread.currentThread().getName())) {
            // only do this on chunk save thread
            try {
                original.call(key, value, out);
            } catch (NBTTagCompoundConcurrentModificationException ex) {
                NBTTagCompound thiz = (NBTTagCompound) ((Object) this);
                if (thiz.hasKey("id", Constants.NBT.TAG_STRING)) {
                    ex.addKeyPath(String.format("[id=%s]%s", thiz.getString("id"), key));
                } else {
                    ex.addKeyPath(key);
                }
                throw ex;
            }
        } else {
            original.call(key, value, out);
        }
    }
}
