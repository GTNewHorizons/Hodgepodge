package com.mitchej123.hodgepodge.mixins.late.minecraft;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import net.minecraft.command.CommandTime;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Merged from ModMixins under the MIT License
 *    Copyright bartimaeusnek & GTNewHorizons
 */
@Mixin(CommandTime.class)
public class MixinTimeCommandGalacticraftFix {
    @Inject(method = "setTime", at = @At("HEAD"), cancellable = true)
    protected final void setTime(ICommandSender p_71552_1_, int p_71552_2_, CallbackInfo x) {
        for (WorldServer server : MinecraftServer.getServer().worldServers) {
            if (server.provider instanceof WorldProviderSpace) {
                ((WorldProviderSpace) server.provider).setWorldTimeCommand(p_71552_2_);
            } else {
                server.setWorldTime(p_71552_2_);
            }
        }

        x.cancel();
    }

    @Inject(method = "addTime", at = @At("HEAD"), cancellable = true)
    protected final void addTime(ICommandSender p_71553_1_, int p_71553_2_, CallbackInfo x) {
        for (WorldServer server : MinecraftServer.getServer().worldServers) {
            if (server.provider instanceof WorldProviderSpace) {
                final WorldProviderSpace provider = (WorldProviderSpace) server.provider;
                provider.setWorldTimeCommand(provider.getWorldTimeCommand() + p_71553_2_);
            } else {
                server.setWorldTime(server.getWorldTime() + p_71553_2_);
            }
        }

        x.cancel();
    }
}
