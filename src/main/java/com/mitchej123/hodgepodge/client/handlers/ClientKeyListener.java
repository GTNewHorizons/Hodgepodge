package com.mitchej123.hodgepodge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.util.AboveHotbarHUD;
import com.mitchej123.hodgepodge.client.HodgepodgeClient;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientKeyListener {

    private static final String fastBlockPlacingEnabled = StatCollector
            .translateToLocal("key.fastBlockPlacing.enabled");
    private static final String fastBlockPlacingDisabled = StatCollector
            .translateToLocal("key.fastBlockPlacing.disabled");
    private static final KeyBinding fastBlockPlacingKey = new KeyBinding(
            "key.fastBlockPlacing.desc",
            Keyboard.KEY_NONE,
            "key.hodgepodge.category");

    public ClientKeyListener() {
        ClientRegistry.registerKeyBinding(fastBlockPlacingKey);
    }

    @SubscribeEvent
    public void keyPressed(InputEvent.KeyInputEvent event) {
        if (TweaksConfig.fastBlockPlacingServerSide && fastBlockPlacingKey.isPressed()) {
            TweaksConfig.fastBlockPlacing = !TweaksConfig.fastBlockPlacing;
            AboveHotbarHUD.renderTextAboveHotbar(
                    (TweaksConfig.fastBlockPlacing ? fastBlockPlacingEnabled : fastBlockPlacingDisabled),
                    40,
                    false,
                    false);
            return;
        }
        if (FixesConfig.speedupAnimations) {
            int key = Keyboard.getEventKey();
            boolean released = !Keyboard.getEventKeyState();
            if (released) {
                if (Minecraft.getMinecraft().gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown()
                        && GuiScreen.isCtrlKeyDown()) {
                    if (key == Keyboard.KEY_N) {
                        HodgepodgeClient.animationsMode.next();
                    } else if (key == Keyboard.KEY_D && DebugConfig.renderDebug) {
                        HodgepodgeClient.renderDebugMode.next();
                    }
                }
            }
        }
    }
}
