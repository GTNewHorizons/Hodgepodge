package com.mitchej123.hodgepodge.client.sound;

import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Installs our codec and sound library.
 * <p>
 * Minecraft registers the stock ones in the SoundManager constructor and fires SoundSetupEvent on the very next line,
 * which is the seam Forge provides for exactly this. It happens before the SoundSystem is constructed, so ours are in
 * place from the first sound onwards and remain in the static plugin registry across reloads.
 */
public class SoundSetupHandler {

    public static void register() {
        MinecraftForge.EVENT_BUS.register(new SoundSetupHandler());
    }

    @SubscribeEvent
    public void onSoundSetup(SoundSetupEvent event) {
        LibraryHodgepodgeOpenAL.register();
        DownmixingOggCodec.register();
    }
}
