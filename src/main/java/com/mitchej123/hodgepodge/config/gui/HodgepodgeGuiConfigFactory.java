package com.mitchej123.hodgepodge.config.gui;

import com.gtnewhorizon.gtnhlib.config.SimpleGuiFactory;
import net.minecraft.client.gui.GuiScreen;

public class HodgepodgeGuiConfigFactory implements SimpleGuiFactory {

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return HodgepdgeGuiConfig.class;
    }
}
