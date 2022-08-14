package com.mitchej123.hodgepodge.client;

import com.mitchej123.hodgepodge.Hodgepodge;
import com.mitchej123.hodgepodge.core.HodgePodgeClient;
import com.mitchej123.hodgepodge.core.LoadingConfig;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class DebugScreenHandler {
    
    public static final DebugScreenHandler INSTANCE = new DebugScreenHandler();
    
    private boolean is64bit;
    private String javaVersion;
    private String javaVendor;
    private String gpuName;
    private String glVersion;
    private String osArch;
    private String osName;
    private String osVersion;
    
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
            event.right.add(2, null); //Empty Line
            event.right.add(3, "Java: " + this.javaVersion + (this.is64bit ? " 64bit (" : " 32bit (") + this.javaVendor + ")");
            event.right.add(4, "GPU: " + this.gpuName);
            event.right.add(5, "OpenGL: " + this.glVersion);
            event.right.add(6, "CPU Cores: " + Runtime.getRuntime().availableProcessors());
            event.right.add(7, "OS: " + this.osName + " (" + this.osVersion + ", " + this.osArch + ")");

            if (Hodgepodge.config.speedupAnimations) {
                event.left.add("animationsMode: " + HodgePodgeClient.animationsMode);
            }
        }
    }
    
    private static boolean check64bit() {
        String[] keys = {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        for (String key : keys) {
            String value = System.getProperty(key);
            if (value != null && value.contains("64")) {
                return true;
            }
        }
        return false;
    }

}
