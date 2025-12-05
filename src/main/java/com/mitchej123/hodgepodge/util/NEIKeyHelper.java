package com.mitchej123.hodgepodge.util;

import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.GTNHLib;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.util.NEIKeyboardUtils;
import cpw.mods.fml.common.Optional;

public class NEIKeyHelper {

    @Optional.Method(modid = "NotEnoughItems")
    public static void sendNEIHelp() {
        GTNHLib.proxy.addMessageToChat(
                new ChatComponentTranslation(
                        "hodgepodge.debug.help.nei.1",
                        NEIKeyboardUtils.getKeyName(
                                NEIKeyboardUtils.unhash(NEIClientConfig.getKeyBinding("world.moboverlay")))));
        GTNHLib.proxy.addMessageToChat(
                new ChatComponentTranslation(
                        "hodgepodge.debug.help.nei.2",
                        NEIKeyboardUtils.getKeyName(
                                NEIKeyboardUtils.unhash(NEIClientConfig.getKeyBinding("world.chunkoverlay")))));
    }
}
