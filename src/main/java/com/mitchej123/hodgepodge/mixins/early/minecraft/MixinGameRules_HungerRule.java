package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.world.GameRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.GameRuleExt;

@Mixin(GameRules.class)
public abstract class MixinGameRules_HungerRule implements GameRuleExt {

    @Unique
    private boolean hodgepodge$disableHunger;

    @Shadow
    public abstract void addGameRule(String p_82769_1_, String p_82769_2_);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void hodgepodge$hungerRule(CallbackInfo ci) {
        this.addGameRule("disableHunger", "false");
    }

    @Inject(
            method = "setOrCreateGameRule",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/GameRules$Value;setValue(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER))
    private void hodgepodge$onValueChange(String ruleName, String value, CallbackInfo ci) {
        if ("disableHunger".equals(ruleName)) {
            hodgepodge$disableHunger = Boolean.parseBoolean(value);
        }
    }

    @Override
    public boolean hodgepodge$isHungerDisabled() {
        return hodgepodge$disableHunger;
    }
}
