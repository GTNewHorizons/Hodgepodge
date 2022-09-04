package com.mitchej123.hodgepodge.mixins.minecraft;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft_ToggleDebugMessage {

    @Shadow
    public GameSettings gameSettings;

    @Inject(
            method = "runTick",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/settings/GameSettings;advancedItemTooltips:Z",
                            opcode = Opcodes.PUTFIELD,
                            shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgTooltips(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat("Advanced Item Tooltips:"
                + (gameSettings.advancedItemTooltips
                        ? EnumChatFormatting.GREEN + " On"
                        : EnumChatFormatting.RED + " Off"));
    }

    @Inject(
            method = "runTick",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/renderer/entity/RenderManager;debugBoundingBox:Z",
                            opcode = Opcodes.PUTSTATIC,
                            shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgHitbox(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat("Hitboxes:"
                + (RenderManager.debugBoundingBox
                        ? EnumChatFormatting.GREEN + " On"
                        : EnumChatFormatting.RED + " Off"));
    }

    @Inject(
            method = "runTick",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/client/settings/GameSettings;pauseOnLostFocus:Z",
                            opcode = Opcodes.PUTFIELD,
                            shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgPauseLostFocus(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat("Pause on lost focus:"
                + (gameSettings.pauseOnLostFocus ? EnumChatFormatting.GREEN + " On" : EnumChatFormatting.RED + " Off"));
    }
}
