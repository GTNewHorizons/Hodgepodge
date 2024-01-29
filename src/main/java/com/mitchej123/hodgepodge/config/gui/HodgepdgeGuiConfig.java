package com.mitchej123.hodgepodge.config.gui;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;
import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.OverallConfig;
import com.mitchej123.hodgepodge.config.PollutionConfig;
import com.mitchej123.hodgepodge.config.PollutionRecolorConfig;
import com.mitchej123.hodgepodge.config.SpeedupsConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import net.minecraft.client.gui.GuiScreen;

public class HodgepdgeGuiConfig extends SimpleGuiConfig {

    public HodgepdgeGuiConfig(GuiScreen parent) throws ConfigException {
        super(
                parent,
                "hodgepodge",
                "hodgepodge",
                ASMConfig.class,
                DebugConfig.class,
                FixesConfig.class,
                OverallConfig.class,
                PollutionConfig.class,
                PollutionRecolorConfig.class,
                SpeedupsConfig.class,
                TweaksConfig.class);
    }
}
