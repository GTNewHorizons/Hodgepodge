package com.mitchej123.hodgepodge.client.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.GuiScreenEvent;

import com.mitchej123.hodgepodge.client.gui.HodgepodgeSoundOptionsGui;
import com.mitchej123.hodgepodge.config.TweaksConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ReloadSoundsGui {

    private static final int RELOAD_BUTTON_ID = 51861;
    private static final int OPTIONS_BUTTON_ID = 51862;

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiScreenOptionsSounds) {
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
            int posX = event.gui.width / 2 - 155;
            int optionsWidth = TweaksConfig.reloadSoundsButton ? 150 : 310;
            event.buttonList.add(
                    new GuiButton(
                            OPTIONS_BUTTON_ID,
                            posX,
                            posY,
                            optionsWidth,
                            20,
                            StatCollector.translateToLocal("hodgepodge.soundsmenu.enhancements")));
            if (TweaksConfig.reloadSoundsButton) {
                event.buttonList.add(
                        new GuiButton(
                                RELOAD_BUTTON_ID,
                                posX + 160,
                                posY,
                                150,
                                20,
                                StatCollector.translateToLocal("hodgepodge.soundsmenu.refreshsounds")));
            }
        }
    }

    @SubscribeEvent
    public void onClick(GuiScreenEvent.ActionPerformedEvent.Post event) {
        if (event.gui instanceof GuiScreenOptionsSounds) {
            if (event.button.id == OPTIONS_BUTTON_ID) {
                Minecraft.getMinecraft().displayGuiScreen(new HodgepodgeSoundOptionsGui(event.gui));
            } else if (TweaksConfig.reloadSoundsButton && event.button.id == RELOAD_BUTTON_ID) {
                reloadSounds();
            }
        }
    }

    public static void reloadSounds() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getSoundHandler().onResourceManagerReload(mc.getResourceManager());
    }
}
