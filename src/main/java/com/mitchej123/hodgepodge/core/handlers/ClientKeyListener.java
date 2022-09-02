package com.mitchej123.hodgepodge.core.handlers;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class ClientKeyListener {

    @SubscribeEvent
    public void keyUp(InputEvent.KeyInputEvent event) {
        int key = Keyboard.getEventKey();
        boolean released = !Keyboard.getEventKeyState();
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown() && released) {
            if (key == Keyboard.KEY_N) {
                HodgePodgeClient.animationsMode =
                        HodgePodgeClient.animationsMode == 2 ? 0 : ++HodgePodgeClient.animationsMode;
            } else if (key == Keyboard.KEY_D && Hodgepodge.config.renderDebug) {
                HodgePodgeClient.renderDebugMode =
                        HodgePodgeClient.renderDebugMode == 2 ? 0 : ++HodgePodgeClient.renderDebugMode;
            }
        }
    }
}
