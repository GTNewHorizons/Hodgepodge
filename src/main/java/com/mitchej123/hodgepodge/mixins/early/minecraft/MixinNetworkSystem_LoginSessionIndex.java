package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkSystem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.util.LoginSessionIndex;

@Mixin(NetworkSystem.class)
public class MixinNetworkSystem_LoginSessionIndex implements LoginSessionIndex.Provider {

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @Shadow
    @Final
    private List<NetworkManager> networkManagers;

    @Unique
    private LoginSessionIndex hodgepodge$loginSessionIndex;

    @Inject(method = "networkTick", at = @At("HEAD"))
    private void hodgepodge$discardPreviousTick(CallbackInfo ci) {
        // Do not retain disconnected players indefinitely when no more login attempts arrive.
        this.hodgepodge$loginSessionIndex = null;
    }

    @Override
    public LoginSessionIndex hodgepodge$getLoginSessionIndex() {
        if (this.hodgepodge$loginSessionIndex == null) {
            this.hodgepodge$loginSessionIndex = new LoginSessionIndex(this.mcServer, this.networkManagers);
        }
        return this.hodgepodge$loginSessionIndex;
    }

    @WrapOperation(
            method = "networkTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/INetHandler;onDisconnect(Lnet/minecraft/util/IChatComponent;)V"),
            expect = 2)
    private void hodgepodge$finishDisconnect(INetHandler handler, IChatComponent reason, Operation<Void> original,
            @Local NetworkManager manager) {
        try {
            original.call(handler, reason);
        } finally {
            if (this.hodgepodge$loginSessionIndex != null) {
                this.hodgepodge$loginSessionIndex.connectionRemoved(manager);
            }
        }
    }
}
