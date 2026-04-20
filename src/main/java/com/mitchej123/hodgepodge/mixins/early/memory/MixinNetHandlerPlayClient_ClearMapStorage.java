package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.storage.MapStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_ClearMapStorage {

    @Shadow
    public MapStorage mapStorageOrigin;

    @Inject(method = "cleanup", at = @At("HEAD"))
    private void cleanup(CallbackInfo ci) {
        this.mapStorageOrigin = new MapStorage(null);
    }
}
