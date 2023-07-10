package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.net.MalformedURLException;
import java.net.URL;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraftforge/common/ForgeVersion$1")
public abstract class MixinForgeVersion_FixUpdateCheck {

    /**
     * Replace the url with https version to fix connection
     */
    // MC-Dev plugin can't handle inner classes, resulting in incorrect errors
    @Redirect(method = "run()V", at = @At(value = "NEW", target = "(Ljava/lang/String;)Ljava/net/URL;"), remap = false)
    private URL redirectVersionCheck(String dud) throws MalformedURLException {
        return new URL("https://files.minecraftforge.net/maven/net/minecraftforge/forge/promotions_slim.json");
    }
}
