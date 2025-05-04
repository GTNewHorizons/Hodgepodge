package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

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

@Mixin(NBTTagList.class)
public class MixinNBTTagList_cacheHash implements NBTCachedHash {

    @Shadow
    private List<NBTBase> tagList;
    @Unique
    private int hodgepodge$cachedHash;

    @Override
    public void hodgepodge$setCachedHash(int hash) {
        this.hodgepodge$cachedHash = hash;
    }

    @Inject(method = "func_152446_a", at = @At("RETURN"))
    private void hodgepodge$clearHashOnRead(CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(method = "appendTag", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private void hodgepodge$clearHashOnAdd(CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(
            method = "func_150304_a",
            at = @At(value = "INVOKE", target = "Ljava/util/List;set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnSet(CallbackInfo ci) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(method = "removeTag", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(I)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnRemove(CallbackInfoReturnable<NBTBase> cir) {
        hodgepodge$cachedHash = 0;
    }

    @Inject(method = { "getCompoundTagAt", "func_150306_c", // getIntArray
    }, at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private void hodgepodge$clearHashOnMutGet(CallbackInfoReturnable<?> cir) {
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
            hodgepodge$cachedHash = super.hashCode() ^ this.tagList.hashCode();
        }
        return hodgepodge$cachedHash;
    }
}
