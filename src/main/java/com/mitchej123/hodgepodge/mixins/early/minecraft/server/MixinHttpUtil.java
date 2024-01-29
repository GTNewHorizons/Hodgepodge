package com.mitchej123.hodgepodge.mixins.early.minecraft.server;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import net.minecraft.util.HttpUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;
import java.net.ServerSocket;

@Mixin(HttpUtil.class)
public class MixinHttpUtil {

    /**
     * @author laetansky
     * @reason Allow LAN servers to have static port number
     */
    @Overwrite
    public static int func_76181_a() throws IOException {
        ServerSocket serversocket = null;
        int port = 0;

        try {
            serversocket = new ServerSocket(TweaksConfig.defaultLanPort);
        } catch (SecurityException securityException) {
            // Assign an automatically allocated port number
            Common.log.warn(
                    String.format("Designated port %s is already in use, using automatically assigned instead", port));
            serversocket = new ServerSocket(0);
        } finally {
            if (serversocket != null) {
                port = serversocket.getLocalPort();
                serversocket.close();
            }
        }

        return port;
    }
}
