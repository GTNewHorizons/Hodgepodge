package com.mitchej123.hodgepodge.core.rfb;

import java.util.ArrayList;

import net.minecraft.launchwrapper.Launch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.retrofuturabootstrap.api.PluginContext;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbPlugin;
import com.mitchej123.hodgepodge.core.rfb.transformers.ConfigParsingTimeTransformer;
import com.mitchej123.hodgepodge.core.rfb.transformers.ForgeConfigurationTransformer;
import com.mitchej123.hodgepodge.core.shared.EarlyConfig;

public class HodgepodgeRfbPlugin implements RfbPlugin {

    @Override
    public void onConstruction(@NotNull PluginContext ctx) {
        Launch.blackboard.put("hodgepodge.rfbPluginLoaded", Boolean.TRUE);
        EarlyConfig.ensureLoaded();
    }

    @Override
    public @NotNull RfbClassTransformer @Nullable [] makeTransformers() {
        ArrayList<RfbClassTransformer> list = new ArrayList<>();
        if (!EarlyConfig.noLeanerForgeConfiguration) {
            list.add(new ForgeConfigurationTransformer());
        }
        if (EarlyConfig.debugLogConfigParsingTimes) {
            list.add(new ConfigParsingTimeTransformer());
        }
        return list.toArray(new RfbClassTransformer[0]);
    }
}
