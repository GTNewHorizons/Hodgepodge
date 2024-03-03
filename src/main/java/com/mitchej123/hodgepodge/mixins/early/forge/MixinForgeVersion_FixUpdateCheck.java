package com.mitchej123.hodgepodge.mixins.early.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net/minecraftforge/common/ForgeVersion$1")
public abstract class MixinForgeVersion_FixUpdateCheck {

    /**
     * Replace the url with https version to fix connection
     */
    // MC-Dev plugin can't handle inner classes, resulting in incorrect errors
    @ModifyArg(
            at = @At(target = "Ljava/net/URL;<init>(Ljava/lang/String;)V", value = "INVOKE"),
            method = "run",
            remap = false)
    private String hodgepodge$fixProtocol(String spec) {
        return spec.replaceFirst("http://", "https://");
    }
}
