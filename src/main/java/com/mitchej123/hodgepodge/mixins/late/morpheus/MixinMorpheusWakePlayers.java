package com.mitchej123.hodgepodge.mixins.late.morpheus;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.quetzi.morpheus.SleepChecker;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SleepChecker.class, remap = false)
public class MixinMorpheusWakePlayers {

    @Inject(
            method = "advanceToMorning",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldProvider;resetRainAndThunder()V",
                    shift = At.Shift.BEFORE,
                    remap = true))
    private void hodgepodge$fixWakePlayers(World world, CallbackInfo c) {
        if (!(world instanceof WorldServer worldServer)) return;
        worldServer.wakeAllPlayers();
    }
}
