package com.mitchej123.hodgepodge.mixins.late.journeymap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import journeymap.client.data.WorldData;

@Mixin(WorldData.class)
public class MixinWorldData {

    @ModifyReturnValue(method = "getServerName", at = @At("RETURN"), remap = false)
    private static String hodgepodge$fixIllegalFilePathCharacter(String worldname) {
        return worldname == null ? null : worldname.replace(':', '_');
    }
}
