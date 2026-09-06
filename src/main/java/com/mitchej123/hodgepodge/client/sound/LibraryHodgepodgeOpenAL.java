package com.mitchej123.hodgepodge.client.sound;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
 * Drop-in replacement for {@link LibraryLWJGLOpenAL}, installed through Paulscode's own
 * {@link SoundSystemConfig#addLibrary} plugin registry rather than by patching anything.
 * <p>
 * Adds the hooks the stock library lacks for three audio improvements:
 * <ul>
 * <li><b>Release redundant decoded data.</b> {@code loadSound} keeps the decoded PCM in a Java {@code byte[]} after
 * uploading it to OpenAL. Nothing reads the heap copy afterwards, so it is released here. See
 * {@link #loadSound(FilenameURL)}.</li>
 * <li><b>World and UI sounds need different routing.</b> {@link #createChannel(int)} configures positional stereo and
 * direct UI/music playback before a channel starts. See {@link SpatializeSupport}.</li>
 * <li><b>Environmental reverb needs a per-source send.</b> The channel also routes positional sources through the
 * shared effect slot and clears that state when reused. See {@link ReverbSupport}.</li>
 * </ul>
 */
public class LibraryHodgepodgeOpenAL extends LibraryLWJGLOpenAL {

    // Paulscode serializes library commands; this is only set during its synchronous raw-data feed.
    private Source feedingRawSource;

    public LibraryHodgepodgeOpenAL() throws SoundSystemException {
        super();
        // A reload builds a new Library on a new AL context, so anything we cached about the old one is stale.
        // This is the reliable signal: a recreated device can land on the same native pointer, so comparing handles
        // is not enough. Doing it here rather than off a device-change check also keeps it working on Java 8.
        ReverbSupport.invalidate();
        SoundDeviceTweaks.invalidate();
        SpatializeSupport.invalidate();
    }

    /**
     * Registers this in place of the stock library. Minecraft adds {@link LibraryLWJGLOpenAL} in the SoundManager
     * constructor and then fires SoundSetupEvent, and SoundSystem's no-arg constructor walks the registry in order
     * using the first entry that initialises. Removing theirs and adding ours therefore wins every time, including for
     * the SoundSystems ArchaicFix recreates on a device change.
     */
    public static void register() {
        try {
            SoundSystemConfig.removeLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.addLibrary(LibraryHodgepodgeOpenAL.class);
            Common.log.info("Installed Hodgepodge OpenAL sound library");
        } catch (SoundSystemException e) {
            Common.log.error("Could not install the Hodgepodge sound library, leaving audio as-is", e);
        }
    }

    /**
     * Releases the heap-side copy of the audio once OpenAL has it.
     * <p>
     * {@code trimData(0)} is Paulscode's own API for dropping the array. The map entry has to stay, because
     * {@code loadSound} uses its presence as the "already decoded" check. Only the bytes are redundant. This is safe
     * because the only reader of {@code audioData} after upload is streaming pre-load, and streaming sources never come
     * through here.
     */
    @Override
    public boolean loadSound(FilenameURL filenameURL) {
        final boolean loaded = super.loadSound(filenameURL);
        if (loaded && SoundConfig.releaseDecodedSoundData && filenameURL != null) {
            final SoundBuffer buffer = bufferMap.get(filenameURL.getFilename());
            if (buffer != null) buffer.trimData(0);
        }
        return loaded;
    }

    /**
     * Keeps Paulscode's source allocation and failure handling, but configures routing before playback starts.
     * <p>
     * SourceLWJGLOpenAL calls Channel.play() after both sides of the channel assignment are set, for normal sounds and
     * file streams alike. Streams are handed to the preload worker afterwards, so their later direct alSourcePlay calls
     * retain these settings. Raw streams instead start directly inside feedRawAudioData().
     */
    @Override
    protected Channel createChannel(int type) {
        final Channel channel = super.createChannel(type);
        if (!(channel instanceof ChannelLWJGLOpenAL openAL)) return channel;

        // The stock constructor only stores the source handle; the replacement owns its normal cleanup.
        return new ChannelLWJGLOpenAL(type, openAL.ALSource) {

            @Override
            public void play() {
                configureRouting();
                super.play();
            }

            @Override
            public int feedRawAudioData(byte[] buffer) {
                // Library assigns attachedSource after feeding, but the feed itself can already start playback.
                if (feedingRawSource != null && feedingRawSource.channel == this) {
                    attachedSource = feedingRawSource;
                }
                configureRouting();
                return super.feedRawAudioData(buffer);
            }

            private void configureRouting() {
                // A previous owner can retain a stale channel reference after the channel is reassigned.
                if (attachedSource != null && attachedSource.channel == this && ALSource != null) {
                    final int alSource = ALSource.get(0);
                    final boolean positional = attachedSource.attModel != SoundSystemConfig.ATTENUATION_NONE;
                    SpatializeSupport.apply(alSource, positional);
                    ReverbSupport.route(alSource, positional);
                }
            }
        };
    }

    @Override
    public int feedRawAudioData(Source source, byte[] buffer) {
        // As in Library.play(), discard a stale channel reference so Source closes a reassigned channel first.
        if (source != null && source.rawDataStream
                && source.active()
                && source.channel != null
                && source.channel.attachedSource != source) {
            source.channel = null;
        }
        feedingRawSource = source;
        try {
            return super.feedRawAudioData(source, buffer);
        } finally {
            feedingRawSource = null;
        }
    }
}
