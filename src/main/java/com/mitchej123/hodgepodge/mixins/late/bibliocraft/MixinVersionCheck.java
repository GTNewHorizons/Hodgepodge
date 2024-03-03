package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jds.bibliocraft.VersionCheck;

@Mixin(value = VersionCheck.class, remap = false)
public class MixinVersionCheck {

    /**
     * @author Alexdoru
     * @reason yeet
     */
    @Overwrite
    @SubscribeEvent
    public void onWorldLoad(EntityJoinWorldEvent event) {
        FMLCommonHandler.instance().bus().unregister(this);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = { "getNetVersion", "setUpdateMessage" })
    private void hodgepodge$exit(CallbackInfo ci) {
        ci.cancel();
    }
}
