package com.mitchej123.hodgepodge.mixins.late.portalgun;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import portalgun.client.thread.ThreadDownloadResources;

@Mixin(ThreadDownloadResources.class)
public class MixinThreadDownloadResources {

    @ModifyArg(
            at = @At(target = "Ljava/net/URL;<init>(Ljava/lang/String;)V", value = "INVOKE"),
            expect = 2,
            method = "run",
            remap = false)
    private String hodgepodge$fixSpec(String spec) {
        return spec.replace("http://repo.creeperhost.net", "https://dist.creeper.host")
                .replace("http://cdn.redstone.tech", "https://dist.creeper.host");
    }

}
