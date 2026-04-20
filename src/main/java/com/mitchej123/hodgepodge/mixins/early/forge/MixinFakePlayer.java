package com.mitchej123.hodgepodge.mixins.early.forge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.authlib.GameProfile;

@Mixin(FakePlayer.class)
public abstract class MixinFakePlayer extends EntityPlayerMP {

    public MixinFakePlayer(MinecraftServer minecraftServer, WorldServer worldServer, GameProfile gameProfile,
            ItemInWorldManager itemInWorldManager) {
        super(minecraftServer, worldServer, gameProfile, itemInWorldManager);
    }

    @Override
    public void addChatMessage(IChatComponent message) {}
}
