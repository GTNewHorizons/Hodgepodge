package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.awt.Desktop;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks {

    @Inject(
            method = "actionPerformed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Runtime;getRuntime()Ljava/lang/Runtime;",
                    ordinal = 1,
                    shift = At.Shift.BEFORE),
            cancellable = true)
    private void hodgepodge$fixFolderOpening(CallbackInfo ci) throws IOException {
        Desktop.getDesktop().open(Minecraft.getMinecraft().getResourcePackRepository().getDirResourcepacks());
        ci.cancel();
    }
}
