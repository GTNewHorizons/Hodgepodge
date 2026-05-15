package com.mitchej123.hodgepodge.mixins.early.memory;

import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FakePlayerFactory.class, remap = false)
public class MixinFakePlayerFactory_FixLeak {

    @Shadow
    private static FakePlayer MINECRAFT_PLAYER;

    @Inject(method = "unloadWorld", at = @At("HEAD"))
    private static void clearMinecraftPlayer(WorldServer world, CallbackInfo ci) {
        if (MINECRAFT_PLAYER != null && MINECRAFT_PLAYER.worldObj == world) {
            MINECRAFT_PLAYER = null;
        }
    }
}
