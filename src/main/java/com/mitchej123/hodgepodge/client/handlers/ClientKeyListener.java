package com.mitchej123.hodgepodge.client.handlers;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.gtnewhorizon.gtnhlib.util.AboveHotbarHUD;
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
            ConfigurationManager.save(TweaksConfig.class);
            AboveHotbarHUD.renderTextAboveHotbar(
                    (TweaksConfig.fastBlockPlacing ? fastBlockPlacingEnabled : fastBlockPlacingDisabled),
                    40,
                    false,
                    false);
            return;
        }
    }
}
