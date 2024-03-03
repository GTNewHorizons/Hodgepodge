package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.settings.GameSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(GameSettings.class)
public class MixinGameSettings_SprintKey {

    @ModifyConstant(
            method = { "<init>()V", "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V" },
            constant = @Constant(stringValue = "key.categories.gameplay"),
            slice = @Slice(
                    from = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/settings/GameSettings;keyBindPickBlock:Lnet/minecraft/client/settings/KeyBinding;"),
                    to = @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/settings/GameSettings;keyBindSprint:Lnet/minecraft/client/settings/KeyBinding;")))
    private String hodgepodge$ChangeSprintCategory(String original) {
        return "key.categories.movement";
    }

}
