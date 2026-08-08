package com.mitchej123.hodgepodge.client;

import java.util.List;

import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;

import com.mitchej123.hodgepodge.Common;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Backport of the modern --quickPlaySingleplayer / --quickPlayMultiplayer launch arguments.
 * <ul>
 * <li>Also accepted as JVM args (-DquickPlaySingleplayer=...)
 * <li>The world is a save folder, a display name, or "latest".
 * </ul>
 */
public class QuickPlay {

    private static final String LATEST = "latest";

    private final String world;
    private final String address;
    private boolean fired;

    private QuickPlay(String world, String address) {
        this.world = world;
        this.address = address;
    }

    public static void registerIfRequested() {
        final String world = resolve("hodgepodge.quickPlay.singleplayer", "quickPlaySingleplayer");
        String address = resolve("hodgepodge.quickPlay.multiplayer", "quickPlayMultiplayer");
        if (world == null && address == null) return;

        if (world != null && address != null) {
            Common.log.warn("Quick play: both a world and a server were requested, ignoring the server '{}'", address);
            address = null;
        }
        FMLCommonHandler.instance().bus().register(new QuickPlay(world, address));
    }

    // Game args win over JVM args
    private static String resolve(String blackboardKey, String property) {
        final Object arg = Launch.blackboard.get(blackboardKey);
        final String value = arg instanceof String s ? s : System.getProperty(property);
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (fired || event.phase != TickEvent.Phase.END) return;

        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null || !(mc.currentScreen instanceof GuiMainMenu)) return;

        fired = true;
        FMLCommonHandler.instance().bus().unregister(this);

        if (world != null) {
            loadWorld(mc, world);
        } else {
            connect(address);
        }
    }

    private static void loadWorld(Minecraft mc, String target) {
        final ISaveFormat saves = mc.getSaveLoader();
        final List<SaveFormatComparator> saveList;
        try {
            saveList = saves.getSaveList();
        } catch (AnvilConverterException e) {
            Common.log.error("Quick play: could not read the save list", e);
            return;
        }

        final SaveFormatComparator save = find(saveList, target);
        if (save == null) {
            Common.log.error("Quick play: no save matching '{}'", target);
            return;
        }
        if (save.requiresConversion()) {
            Common.log.error(
                    "Quick play: save '{}' uses the old map format, open it once from the world list to convert it",
                    save.getFileName());
            return;
        }
        if (!saves.canLoadWorld(save.getFileName())) {
            Common.log.error("Quick play: save '{}' cannot be loaded", save.getFileName());
            return;
        }

        Common.log.info("Quick play: loading world '{}'", save.getFileName());

        FMLClientHandler.instance()
                .tryLoadExistingWorld(new GuiSelectWorld(new GuiMainMenu()), save.getFileName(), save.getDisplayName());
    }

    private static SaveFormatComparator find(List<SaveFormatComparator> saves, String target) {
        if (LATEST.equalsIgnoreCase(target)) {
            SaveFormatComparator newest = null;
            for (SaveFormatComparator save : saves) {
                if (newest == null || save.getLastTimePlayed() > newest.getLastTimePlayed()) {
                    newest = save;
                }
            }
            return newest;
        }
        for (SaveFormatComparator save : saves) {
            if (save.getFileName().equals(target)) return save;
        }
        for (SaveFormatComparator save : saves) {
            if (save.getDisplayName().equalsIgnoreCase(target)) return save;
        }
        return null;
    }

    private static void connect(String address) {
        Common.log.info("Quick play: connecting to '{}'", address);

        final FMLClientHandler fml = FMLClientHandler.instance();
        fml.setupServerList();
        fml.connectToServer(new GuiMainMenu(), new ServerData("Quick Play", address));
    }
}
