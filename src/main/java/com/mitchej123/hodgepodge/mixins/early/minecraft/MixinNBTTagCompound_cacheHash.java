package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mitchej123.hodgepodge.mixins.interfaces.NBTCachedHash;

@Mixin(NBTTagCompound.class)
public abstract class MixinNBTTagCompound_cacheHash implements NBTCachedHash {

    @Shadow
    private Map<String, NBTBase> tagMap;

    @Unique
    private int hodgepodge$cachedHash;

    @Override
    public void hodgepodge$setCachedHash(int hash) {
        this.hodgepodge$cachedHash = hash;
    }

    @Inject(
            method = { "setTag", "setByte", "setShort", "setInteger", "setLong", "setFloat", "setDouble", "setString",
                    "setByteArray", "setIntArray",
            // "setBoolean", // already calls setByte
            },
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnSet(CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(
            method = { "getTag", "getByteArray", "getIntArray", "getCompoundTag", "getTagList", },
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnMutGet(CallbackInfoReturnable<?> cir) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(
            method = "removeTag",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnRemove(String key, CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(method = "func_152446_a", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
    private void hodgepodge$clearHashOnClear(CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(method = "func_150296_c", at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;"))
    private void hodgepodge$clearHashOnMutGetKeySet(CallbackInfoReturnable<NBTTagCompound> cir) {
        hodgepodge$cachedHash = 0;
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private NBTBase hodgepodge$copyHash(NBTBase original) {
        ((NBTCachedHash) original).hodgepodge$setCachedHash(hodgepodge$cachedHash);
        return original;
    }

    @ModifyExpressionValue(
            method = "equals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTBase;equals(Ljava/lang/Object;)Z"))
    private boolean hodgepodge$testCachedHash(boolean original, Object other) {
        return original && other.hashCode() == this.hashCode();
    }

    /**
     * @author kurrycat
     * @reason Use cached hash code
     */
    @Overwrite
    public int hashCode() {
        if (hodgepodge$cachedHash == 0) {
            hodgepodge$cachedHash = super.hashCode() ^ this.tagMap.hashCode();
        }
        return hodgepodge$cachedHash;
    }
}
