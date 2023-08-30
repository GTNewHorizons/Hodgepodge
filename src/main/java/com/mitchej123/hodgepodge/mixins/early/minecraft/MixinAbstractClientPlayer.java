package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.entity.AbstractClientPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractClientPlayer.class)
public class MixinAbstractClientPlayer {

    @ModifyConstant(
            method = "getDownloadImageSkin",
            constant = @Constant(stringValue = "http://skins.minecraft.net/MinecraftSkins/%s.png"))
    private static String hodgepodge$redirectSkinUrl(String url) {
        return "https://visage.surgeplay.com/skin/%s.png";
    }
}
