package com.mitchej123.hodgepodge.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiYesNoMultiline extends GuiYesNo {

    private List<String> wrappedLines = new ArrayList<>();

    public GuiYesNoMultiline(GuiYesNoCallback callback, String line1, String line2, int id) {
        super(callback, line1, line2, id);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.wrappedLines = this.fontRendererObj.listFormattedStringToWidth(this.field_146354_r, this.width - 50);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146351_f, this.width / 2, 70, 16777215);

        int y = 90;
        for (String line : this.wrappedLines) {
            this.drawCenteredString(this.fontRendererObj, line, this.width / 2, y, 16777215);
            y += this.fontRendererObj.FONT_HEIGHT;
        }

        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(this.mc, mouseX, mouseY);
        }
    }
}
