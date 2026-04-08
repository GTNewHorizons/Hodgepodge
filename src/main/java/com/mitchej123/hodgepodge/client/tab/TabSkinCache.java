package com.mitchej123.hodgepodge.client.tab;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Caches skin ResourceLocations for tab list players. Triggers async download via
 * AbstractClientPlayer.getDownloadImageSkin and returns the location (steve until loaded).
 */
public class TabSkinCache {

    public static final TabSkinCache INSTANCE = new TabSkinCache();

    private final Map<String, ResourceLocation> cache = new HashMap<>();

    private TabSkinCache() {}

    /**
     * Returns the skin ResourceLocation for the given player name. If the skin hasn't been requested yet, triggers an
     * async download. Returns the location immediately - it will resolve to steve.png until the real skin loads.
     */
    public ResourceLocation getOrLoadSkin(String playerName) {
        ResourceLocation loc = cache.get(playerName);
        if (loc == null) {
            loc = AbstractClientPlayer.getLocationSkin(playerName);
            AbstractClientPlayer.getDownloadImageSkin(loc, playerName);
            cache.put(playerName, loc);
        }
        return loc;
    }

    /**
     * Removes entries for players no longer in the tab list.
     */
    public void cleanup(Collection<String> activePlayers) {
        Iterator<String> it = cache.keySet().iterator();
        while (it.hasNext()) {
            if (!activePlayers.contains(it.next())) {
                it.remove();
            }
        }
    }

    /**
     * Clears all cached skins. Called on disconnect.
     */
    public void clear() {
        cache.clear();
    }
}
