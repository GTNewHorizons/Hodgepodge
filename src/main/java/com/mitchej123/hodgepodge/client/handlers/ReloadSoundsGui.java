package com.mitchej123.hodgepodge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;

import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ReloadSoundsGui {

    private static final int BUTTON_ID = 51861;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (TweaksConfig.reloadSoundsButton && event.gui instanceof GuiScreenOptionsSounds) {
            int posX = event.gui.width / 2 - 100;
            int posY = 0;
            for (Object o : event.buttonList) {
                if (o instanceof GuiButton guiButton
                        && o.getClass().getName().endsWith("GuiScreenOptionsSounds$Button")) {
                    posY = Math.max(posY, guiButton.yPosition);
                }
            }
            if (posY == 0) {
                posY = event.gui.height / 6 + 168 - (20 + 4) * 2;
            }
            posY += 20 + 4;
            event.buttonList.add(
                    new GuiButton(
                            BUTTON_ID,
                            posX,
                            posY,
                            StatCollector.translateToLocal("hodgepodge.soundsmenu.refreshsounds")));
        }
    }

    @SubscribeEvent
    public void onClick(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (TweaksConfig.reloadSoundsButton && event.gui instanceof GuiScreenOptionsSounds) {
            if (event.button.id == BUTTON_ID) {
                Minecraft mc = Minecraft.getMinecraft();
                mc.getSoundHandler().onResourceManagerReload(mc.getResourceManager());
            }
        }
    }

}
