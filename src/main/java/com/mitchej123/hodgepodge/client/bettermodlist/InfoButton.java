package com.mitchej123.hodgepodge.client.bettermodlist;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import com.mitchej123.hodgepodge.Hodgepodge;

import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ModContainer;

public class InfoButton extends GuiButton {

    private static final int COLLAPSED_WIDTH = 20;

    private final GuiModList guiModList;
    private final Supplier<ModContainer> selectedModSupplier;

    public InfoButton(GuiModList gui, Supplier<ModContainer> selectedModSupplier) {
        super(30, gui.width - COLLAPSED_WIDTH - 2, 2, COLLAPSED_WIDTH, COLLAPSED_WIDTH, "?");
        this.guiModList = gui;
        this.selectedModSupplier = selectedModSupplier;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.field_146123_n) {
            ModContainer sel = selectedModSupplier.get();
            this.displayString = (sel != null && sel.getName().equals(Hodgepodge.NAME))
                    ? I18n.format("bettermodlist.gui.modlistinfo2")
                    : I18n.format("bettermodlist.gui.modlistinfo1");

            this.width = mc.fontRenderer.getStringWidth(this.displayString) + 10;
            if (this.width % 2 != 0) this.width++;
        } else {
            this.displayString = "?";
            this.width = COLLAPSED_WIDTH;
        }

        this.xPosition = guiModList.width - this.width - 2;
        super.drawButton(mc, mouseX, mouseY);
    }
}
