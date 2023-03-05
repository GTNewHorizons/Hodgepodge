package com.mitchej123.hodgepodge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.client.HodgepodgeClient;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientKeyListener {

    @SubscribeEvent
    public void keyUp(InputEvent.KeyInputEvent event) {
        int key = Keyboard.getEventKey();
        boolean released = !Keyboard.getEventKeyState();
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown()
                && GuiScreen.isCtrlKeyDown()
                && released) {
            if (key == Keyboard.KEY_N) {
                HodgepodgeClient.animationsMode.next();
            } else if (key == Keyboard.KEY_D && Common.config.renderDebug) {
                HodgepodgeClient.renderDebugMode.next();
            }
        }
    }
}
