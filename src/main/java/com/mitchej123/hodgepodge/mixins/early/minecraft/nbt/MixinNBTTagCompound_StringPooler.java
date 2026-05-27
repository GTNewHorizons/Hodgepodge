package com.mitchej123.hodgepodge.mixins.early.minecraft.nbt;

import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mitchej123.hodgepodge.util.StringPooler;

@Mixin(NBTTagCompound.class)
public class MixinNBTTagCompound_StringPooler {

    @Definition(id = "tagMap", field = "Lnet/minecraft/nbt/NBTTagCompound;tagMap:Ljava/util/Map;")
    @Definition(id = "put", method = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
    @Expression("this.tagMap.put(@(?), ?)")
    @ModifyExpressionValue(
            method = { "func_152446_a", "setTag", "setByte", "setShort", "setInteger", "setLong", "setFloat",
                    "setDouble", "setString", "setByteArray", "setIntArray" },
            at = @At("MIXINEXTRAS:EXPRESSION"))
    private String poolString(String s) {
        // Pool the keys, they're likely to be reused a lot
        return StringPooler.INSTANCE.getString(s);
    }
}
