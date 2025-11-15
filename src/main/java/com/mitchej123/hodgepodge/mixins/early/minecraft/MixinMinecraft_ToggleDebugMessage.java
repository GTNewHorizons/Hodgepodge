package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.GTNHLib;
import com.mitchej123.hodgepodge.util.NEIKeyHelper;

import cpw.mods.fml.common.Loader;

@Mixin(Minecraft.class)
public class MixinMinecraft_ToggleDebugMessage {

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public GuiScreen currentScreen;

    @Unique
    private static int secondsBeforeCrash = 0;

    @Unique
    private static long refreshStartTime = -1;

    @Shadow
    private long field_83002_am;

    @Shadow
    public GuiIngame ingameGUI;

    @Inject(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/settings/GameSettings;advancedItemTooltips:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgTooltips(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat(
                StatCollector.translateToLocal(
                        gameSettings.advancedItemTooltips ? "hodgepodge.debug.advanced_tooltips.enabled"
                                : "hodgepodge.debug.advanced_tooltips.disabled"));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/entity/RenderManager;debugBoundingBox:Z",
                    opcode = Opcodes.PUTSTATIC,
                    shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgHitbox(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat(
                StatCollector.translateToLocal(
                        RenderManager.debugBoundingBox ? "hodgepodge.debug.hitboxes.enabled"
                                : "hodgepodge.debug.hitboxes.disabled"));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/settings/GameSettings;pauseOnLostFocus:Z",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgPauseLostFocus(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat(
                StatCollector.translateToLocal(
                        gameSettings.pauseOnLostFocus ? "hodgepodge.debug.pause_focus.enabled"
                                : "hodgepodge.debug.pause_focus.disabled"));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;loadRenderers()V",
                    shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgChunkReload(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat(StatCollector.translateToLocal("hodgepodge.debug.reload_chunks.message"));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;refreshResources()V",
                    shift = At.Shift.BEFORE))
    public void hodgepodge$printDebugChatMsgRefreshResourceStart(CallbackInfo ci) {
        refreshStartTime = Minecraft.getSystemTime();
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;refreshResources()V",
                    shift = At.Shift.AFTER))
    public void hodgepodge$printDebugChatMsgRefreshResourceEnd(CallbackInfo ci) {
        GTNHLib.proxy.addDebugToChat(
                new ChatComponentTranslation(
                        "hodgepodge.debug.reload_pack.message",
                        (double) (Minecraft.getSystemTime() - refreshStartTime) / 1000d));
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;func_152348_aa()V",
                    shift = At.Shift.AFTER))
    public void hodgepodge$addNewF3Logic(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && currentScreen == null && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_Q:
                    GTNHLib.proxy.addDebugToChat(StatCollector.translateToLocal("hodgepodge.debug.help.message"));
                    GTNHLib.proxy.addMessageToChat(
                            new ChatComponentText(StatCollector.translateToLocal("hodgepodge.debug.help." + 0)));
                    GTNHLib.proxy.addMessageToChat(
                            new ChatComponentText(StatCollector.translateToLocal("hodgepodge.debug.help." + 1)));
                    if (Loader.isModLoaded("angelica")) {
                        GTNHLib.proxy.addMessageToChat(
                                new ChatComponentText(
                                        StatCollector.translateToLocal("hodgepodge.debug.help.angelica")));
                    }
                    for (int i = 2; i < 10; i++) {
                        GTNHLib.proxy.addMessageToChat(
                                new ChatComponentText(StatCollector.translateToLocal("hodgepodge.debug.help." + i)));
                    }
                    if (Loader.isModLoaded("etfuturum")) {
                        GTNHLib.proxy.addMessageToChat(
                                new ChatComponentText(
                                        StatCollector.translateToLocal("hodgepodge.debug.help.etfuturum")));
                    }
                    if (Loader.isModLoaded("NotEnoughItems")) {
                        NEIKeyHelper.sendNEIHelp();
                    }
                    break;
                case Keyboard.KEY_D:
                    GuiNewChat chat = this.ingameGUI.getChatGUI();
                    chat.clearChatMessages();
                    break;
            }
        }
    }

    @Inject(
            method = "runTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/profiler/Profiler;endSection()V",
                    shift = At.Shift.AFTER,
                    ordinal = 1))
    public void hodgepodge$countBeforeCrash(CallbackInfo ci) {
        if (field_83002_am > 0 && Minecraft.getSystemTime() - field_83002_am > secondsBeforeCrash * 1000L
                && secondsBeforeCrash > -1) {
            GTNHLib.proxy.addDebugToChat(
                    StatCollector.translateToLocal("hodgepodge.debug.crash.count." + secondsBeforeCrash));
            if (secondsBeforeCrash == 6) secondsBeforeCrash = -1;
            else secondsBeforeCrash++;
        } else if (field_83002_am < 0 && secondsBeforeCrash != 0) {
            if (secondsBeforeCrash > 0)
                GTNHLib.proxy.addDebugToChat(StatCollector.translateToLocal("hodgepodge.debug.crash.cancel"));
            secondsBeforeCrash = 0;
        }
    }
}
