package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.common.Loader;

@Mixin(value = GuiSelectWorld.class)
public abstract class MixinGuiSelectWorld extends GuiScreen {

    @Shadow
    private boolean field_146643_x;

    @Shadow
    private List field_146639_s;

    @Inject(method = "confirmClicked", at = @At("HEAD"), cancellable = true)
    public void onConfirmClicked(boolean result, int id, CallbackInfo ci) {

        if (this.field_146643_x) {
            this.field_146643_x = false;

            if (result && !ci.isCancelled()) {
                String McDataDir = Minecraft.getMinecraft().mcDataDir.getPath();
                String fileName = ((SaveFormatComparator) this.field_146639_s.get(id)).getFileName();

                ISaveFormat iSaveFormat = this.mc.getSaveLoader();
                iSaveFormat.flushCache();
                iSaveFormat.deleteWorldDirectory(fileName);

                if (Loader.isModLoaded("journeymap")) {
                    Path jmPath = Paths.get(McDataDir + "/journeymap/data/sp/" + fileName);
                    if (Files.isDirectory(jmPath)) {
                        Common.log.debug("Removing associated JourneyMap data");
                        try {
                            FileUtils.deleteDirectory(jmPath.toFile());
                        } catch (IOException e) {
                            Common.log.warn("Failed to delete JourneyMap world data found at {}", jmPath);
                        }
                    }
                }

                if (Loader.isModLoaded("tcnodetracker")) {
                    Path tcPath = Paths.get(McDataDir + "/TCNodeTracker/" + fileName);
                    if (Files.isDirectory(tcPath)) {
                        Common.log.debug("Removing associated TCNodeTracker data");
                        try {
                            FileUtils.deleteDirectory(tcPath.toFile());
                        } catch (IOException e) {
                            Common.log.warn("Failed to delete TCNodeTracker data found at {}", tcPath);
                        }
                    }
                }

                ci.cancel();
            }
            this.mc.displayGuiScreen(this);
        }
    }
}
