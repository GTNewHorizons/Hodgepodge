package com.mitchej123.hodgepodge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TransferClientHandler {

    private static final Logger LOGGER = LogManager.getLogger("Hodgepodge/Transfer");

    public static void execute(String host, int port) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.func_152344_a(() -> {
            if (mc.func_147104_D() == null) {
                LOGGER.warn("Ignoring transfer request in singleplayer");
                return;
            }

            // Don't disconnect or call loadWorld(null) here! Doing so nulls theWorld
            // while updateController() can still run later this tick (via mixin-injected
            // code paths), causing NPE when stale packets access the null world.
            //
            // Instead, just show GuiTransferring. Its updateScreen() runs AFTER
            // updateController() in the tick cycle (line 1752 vs 1693 in runTick),
            // so when it creates GuiConnecting (which calls loadWorld(null)),
            // updateController has already finished for this tick. Next tick,
            // theWorld is null and updateController is skipped entirely.
            String address = port == 25565 ? host : host + ":" + port;
            ServerData serverData = new ServerData("Transfer", address);
            mc.displayGuiScreen(new GuiTransferring(serverData));
        });
    }

    @SideOnly(Side.CLIENT)
    static class GuiTransferring extends GuiScreen {

        private final ServerData serverData;

        GuiTransferring(ServerData serverData) {
            this.serverData = serverData;
        }

        @Override
        public void updateScreen() {
            // This runs after updateController() in the tick, so it's safe to
            // create GuiConnecting which calls loadWorld(null) internally.
            this.mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this.mc, this.serverData));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(
                    this.fontRendererObj,
                    "Transferring...",
                    this.width / 2,
                    this.height / 2 - 50,
                    0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
