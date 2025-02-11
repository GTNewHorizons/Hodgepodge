package com.mitchej123.hodgepodge.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.opengl.GL11;

import com.mitchej123.hodgepodge.config.DebugConfig;
import com.mitchej123.hodgepodge.config.FixesConfig;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class DebugScreenHandler {

    public static final DebugScreenHandler INSTANCE = new DebugScreenHandler();

    private final boolean is64bit;
    private final String javaVersion;
    private final String javaVendor;
    private final String gpuName;
    private final String glVersion;
    private final String osArch;
    private final String osName;
    private final String osVersion;

    private DebugScreenHandler() {
        this.is64bit = check64bit();
        this.javaVersion = System.getProperty("java.version");
        this.javaVendor = System.getProperty("java.vendor");
        this.gpuName = GL11.glGetString(GL11.GL_RENDERER);
        this.glVersion = GL11.glGetString(GL11.GL_VERSION);
        this.osArch = System.getProperty("os.arch");
        this.osName = System.getProperty("os.name");
        this.osVersion = System.getProperty("os.version");
    }

    @SubscribeEvent
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            int offset = event.right.isEmpty() ? 0 : 2;
            event.right.add(offset, null); // Empty Line
            event.right.add(
                    1 + offset,
                    "Java: " + this.javaVersion + (this.is64bit ? " 64bit (" : " 32bit (") + this.javaVendor + ")");
            event.right.add(2 + offset, "GPU: " + this.gpuName);
            event.right.add(3 + offset, "OpenGL: " + this.glVersion);
            event.right.add(4 + offset, "CPU Cores: " + Runtime.getRuntime().availableProcessors());
            event.right.add(5 + offset, "OS: " + this.osName + " (" + this.osVersion + ", " + this.osArch + ")");

            if (DebugConfig.renderDebug) {
                event.right.add(6 + offset, null); // Empty Line
                if (DebugConfig.renderDebug) {
                    event.right.add(8 + offset, "renderDebugMode: " + HodgepodgeClient.renderDebugMode);
                }
            }
        }
    }

    private static boolean check64bit() {
        String[] keys = { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
        for (String key : keys) {
            String value = System.getProperty(key);
            if (value != null && value.contains("64")) {
                return true;
            }
        }
        return false;
    }
}
