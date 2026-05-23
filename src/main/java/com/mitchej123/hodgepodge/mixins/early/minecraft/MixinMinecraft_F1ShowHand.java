package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mitchej123.hodgepodge.client.F1State;

@Mixin(Minecraft.class)
public class MixinMinecraft_F1ShowHand {

    @WrapOperation(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.PUTFIELD,
                    target = "Lnet/minecraft/client/settings/GameSettings;hideGUI:Z"))
    private void hodgepodge$f1Cycle(GameSettings instance, boolean value, Operation<Void> original) {
        F1State.state.cycle();
        boolean shouldHideGui = F1State.state != F1State.SHOW_ALL;
        original.call(instance, shouldHideGui);
    }
}
