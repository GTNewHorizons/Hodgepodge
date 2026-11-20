package com.mitchej123.hodgepodge.util;

import net.minecraft.util.ChatComponentTranslation;

import com.gtnewhorizon.gtnhlib.GTNHLib;

import codechicken.nei.NEIClientConfig;
import cpw.mods.fml.common.Optional;

public class NEIKeyHelper {

    @Optional.Method(modid = "NotEnoughItems")
    public static void sendNEIHelp() {
        sendKeyHelp("hodgepodge.debug.help.nei.1", "world.moboverlay");
        sendKeyHelp("hodgepodge.debug.help.nei.2", "world.chunkoverlay");
    }

    @Optional.Method(modid = "NotEnoughItems")
    private static void sendKeyHelp(String langKey, String keyBind) {
        final String keyName = NEIClientConfig.getKeyName(keyBind);
        if (keyName != null) {
            GTNHLib.proxy.addMessageToChat(new ChatComponentTranslation(langKey, keyName));
        }
    }
}
