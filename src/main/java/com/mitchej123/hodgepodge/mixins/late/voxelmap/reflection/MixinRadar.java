package com.mitchej123.hodgepodge.mixins.late.voxelmap.reflection;

import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thevoxelbox.voxelmap.m;

@Mixin(m.class)
public class MixinRadar {

    @Redirect(
            at = @At(
                    ordinal = 0,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getBatHead(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelBat) var0).batHead;
    }

    @Redirect(
            at = @At(
                    ordinal = 1,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getBlazeHead(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelBlaze) var0).blazeHead;
    }

    @Redirect(
            at = @At(
                    ordinal = 2,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getDragonHead(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelDragon) var0).head;
    }

    @Redirect(
            at = @At(
                    ordinal = 3,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getGhastBody(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelGhast) var0).body;
    }

    @Redirect(
            at = @At(
                    ordinal = 4,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getHorseHead(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelHorse) var0).head;
    }

    @Redirect(
            at = @At(
                    ordinal = 5,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getOcelotHead(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelOcelot) var0).ocelotHead;
    }

    @Redirect(
            at = @At(
                    ordinal = 6,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSilverfishBodyParts0(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSilverfish) var0).silverfishBodyParts;
    }

    @Redirect(
            at = @At(
                    ordinal = 7,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSilverfishBodyParts1(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSilverfish) var0).silverfishBodyParts;
    }

    @Redirect(
            at = @At(
                    ordinal = 8,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSlimeBodies0(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSlime) var0).slimeBodies;
    }

    @Redirect(
            at = @At(
                    ordinal = 9,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSlimeRightEye(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSlime) var0).slimeRightEye;
    }

    @Redirect(
            at = @At(
                    ordinal = 10,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSlimeLeftEye(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSlime) var0).slimeLeftEye;
    }

    @Redirect(
            at = @At(
                    ordinal = 11,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSlimeMouth(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSlime) var0).slimeMouth;
    }

    @Redirect(
            at = @At(
                    ordinal = 12,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSlimeBodies1(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSlime) var0).slimeBodies;
    }

    @Redirect(
            at = @At(
                    ordinal = 13,
                    remap = false,
                    // Object getPrivateFieldValueByType(Object, Class<?>, Class<?>, int)
                    target = "Lcom/thevoxelbox/voxelmap/b/y;do(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/Class;I)Ljava/lang/Object;",
                    value = "INVOKE"),
            // BufferedImage createAutoIconImageFromResourceLocation(Contact, Render, ResourceLocation)
            method = "do(Lcom/thevoxelbox/voxelmap/util/i;Lnet/minecraft/client/renderer/entity/Render;Lnet/minecraft/util/ResourceLocation;)Ljava/awt/image/BufferedImage;",
            remap = false)
    private Object hodgepodge$getSquidBody(Object var0, Class<?> var1, Class<?> var2, int var3) {
        return ((ModelSquid) var0).squidBody;
    }
}
