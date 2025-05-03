package com.mitchej123.hodgepodge.client.bettermodlist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.mixins.interfaces.IGuiModList;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ModContainer;

public class InfoButton extends GuiButton {

    private GuiModList guiModList;

    public InfoButton(GuiModList gui) {
        super(30, gui.width - 22, 2, 20, 20, "?");
        this.guiModList = gui;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.field_146123_n) {
            ModContainer sel = ((IGuiModList) guiModList).selectedMod();
            if (sel != null && sel.getName().equals(Hodgepodge.NAME)) {
                this.displayString = StatCollector.translateToLocal("bettermodlist.gui.modlistinfo2");
            } else {
                this.displayString = StatCollector.translateToLocal("bettermodlist.gui.modlistinfo1");
            }

            this.width = mc.fontRenderer.getStringWidth(this.displayString) + 10;
            if (this.width % 2 != 0) // Fixes the button shifting to the left
            {
                this.width++;
            }

            this.xPosition = guiModList.width - this.width - 2;
        } else {
            this.displayString = "?";
            this.width = 20;
            this.xPosition = guiModList.width - this.width - 2;
        }

        super.drawButton(mc, mouseX, mouseY);
    }
}
