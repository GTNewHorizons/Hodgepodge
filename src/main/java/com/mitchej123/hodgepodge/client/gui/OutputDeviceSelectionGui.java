package com.mitchej123.hodgepodge.client.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.mitchej123.hodgepodge.client.sound.OutputDeviceSupport;
import com.mitchej123.hodgepodge.config.SoundConfig;

/** Selects one of OpenAL's currently available playback endpoints. */
public class OutputDeviceSelectionGui extends GuiScreen {

    private static final int DONE_BUTTON_ID = 200;

    private final GuiScreen parent;
    private DeviceList list;
    private String error;
    private int refreshTicks;

    public OutputDeviceSelectionGui(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        list = new DeviceList(OutputDeviceSupport.devices());
        buttonList.add(new GuiButton(DONE_BUTTON_ID, width / 2 - 100, height - 28, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == DONE_BUTTON_ID) mc.displayGuiScreen(parent);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) mc.displayGuiScreen(parent);
        else super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (++refreshTicks >= 20) {
            refreshTicks = 0;
            list.refresh();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        list.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(
                fontRendererObj,
                I18n.format("hodgepodge.soundsmenu.output_device.title"),
                width / 2,
                12,
                0xFFFFFF);
        if (error != null) drawCenteredString(fontRendererObj, I18n.format(error), width / 2, height - 48, 0xFF5555);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String displayName(String device) {
        return device.isEmpty() ? I18n.format("hodgepodge.soundsmenu.output_device.default")
                : OutputDeviceSupport.displayName(device);
    }

    private class DeviceList extends GuiSlot {

        private final List<String> devices;

        DeviceList(List<String> devices) {
            super(
                    OutputDeviceSelectionGui.this.mc,
                    OutputDeviceSelectionGui.this.width,
                    OutputDeviceSelectionGui.this.height,
                    28,
                    OutputDeviceSelectionGui.this.height - 56,
                    22);
            this.devices = devices;
        }

        void refresh() {
            List<String> current = OutputDeviceSupport.devices();
            if (!devices.equals(current)) {
                devices.clear();
                devices.addAll(current);
            }
        }

        @Override
        protected int getSize() {
            return devices.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            String selected = devices.get(index);
            if (selected.equals(SoundConfig.outputDevice)) {
                error = null;
                return;
            }
            if (!OutputDeviceSupport.select(selected)) {
                error = "hodgepodge.soundsmenu.output_device.failed";
                return;
            }
            SoundConfig.outputDevice = selected;
            ConfigurationManager.save(SoundConfig.class);
            error = null;
        }

        @Override
        protected boolean isSelected(int index) {
            return devices.get(index).equals(SoundConfig.outputDevice);
        }

        @Override
        protected void drawBackground() {
            OutputDeviceSelectionGui.this.drawDefaultBackground();
        }

        @Override
        public int getListWidth() {
            return Math.min(width - 32, 480);
        }

        @Override
        protected int getScrollBarX() {
            return width / 2 + getListWidth() / 2 + 4;
        }

        @Override
        protected void drawSlot(int index, int x, int y, int height, Tessellator tessellator, int mouseX, int mouseY) {
            String name = displayName(devices.get(index));
            String suffix = "...";
            int maxWidth = getListWidth() - 12;
            if (fontRendererObj.getStringWidth(name) > maxWidth) {
                name = fontRendererObj.trimStringToWidth(name, maxWidth - fontRendererObj.getStringWidth(suffix))
                        + suffix;
            }
            drawCenteredString(fontRendererObj, name, width / 2, y + 3, 0xFFFFFF);
        }
    }
}
