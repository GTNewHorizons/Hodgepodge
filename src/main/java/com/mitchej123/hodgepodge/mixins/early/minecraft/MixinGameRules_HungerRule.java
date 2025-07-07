package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public abstract class MixinGameRules_HungerRule {

    @Shadow
    public abstract void addGameRule(String p_82769_1_, String p_82769_2_);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$hungerRule(CallbackInfo ci) {
        this.addGameRule("disableHunger", "false");
    }
}
