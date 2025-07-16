package com.mitchej123.hodgepodge.rfb;

import net.minecraft.launchwrapper.Launch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.retrofuturabootstrap.api.PluginContext;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbPlugin;
import com.mitchej123.hodgepodge.asm.EarlyConfig;

public class HodgepodgeRfbPlugin implements RfbPlugin {

    @Override
    public void onConstruction(@NotNull PluginContext ctx) {
        Launch.blackboard.put("hodgepodge.rfbPluginLoaded", Boolean.TRUE);
        EarlyConfig.ensureLoaded();
    }

    @Override
    public @NotNull RfbClassTransformer @Nullable [] makeTransformers() {
        return new RfbClassTransformer[] { new ForgeConfigurationTransformer(), new DragonAPINBTFix() };
    }
}
