package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.client.F1State;
import com.mitchej123.hodgepodge.client.HodgepodgeClient;

@Mixin(Minecraft.class)
public class MixinMinecraft_F1ShowHand {

    @WrapOperation(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/settings/GameSettings;hideGUI:Z"))
    private void f1Cycle(GameSettings instance, boolean value, Operation<Void> original) {
        HodgepodgeClient.F1_STATE = HodgepodgeClient.F1_STATE.next();
        boolean shouldHideGui = HodgepodgeClient.F1_STATE != F1State.SHOW_ALL;
        original.call(instance, shouldHideGui);
    }
}
