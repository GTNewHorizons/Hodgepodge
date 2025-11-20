package com.mitchej123.hodgepodge.mixins.preinit.embedid;

import com.mitchej123.hodgepodge.hax.embedids.HodgeGameData;
import cpw.mods.fml.common.registry.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameData.class)
public class MixinGameData {

    @Unique
    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "()Lcpw/mods/fml/common/registry/GameData;"))
    private static GameData hodgepodge$replaceGameData() {
        return new HodgeGameData();
    }
}
