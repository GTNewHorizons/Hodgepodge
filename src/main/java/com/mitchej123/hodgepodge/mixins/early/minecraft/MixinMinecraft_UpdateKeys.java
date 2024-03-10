package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.KeyBindingExt;

@Mixin(Minecraft.class)
public class MixinMinecraft_UpdateKeys {

    /**
     * From Sk1er/Patcher
     */
    @Inject(
            method = "setIngameFocus",
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/client/Minecraft;mouseHelper:Lnet/minecraft/util/MouseHelper;",
                    value = "FIELD"))
    private void hodgepodge$updateKeysStates(CallbackInfo ci) {
        ((KeyBindingExt) Minecraft.getMinecraft().gameSettings.keyBindAttack).hodgepodge$updateKeyStates();
    }

}
