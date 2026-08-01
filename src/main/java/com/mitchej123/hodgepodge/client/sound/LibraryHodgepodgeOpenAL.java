package com.mitchej123.hodgepodge.client.sound;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

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
 * Fixes two things the stock library cannot:
 * <ul>
 * <li><b>Sounds are stored twice.</b> {@code loadSound} keeps the decoded PCM in a Java {@code byte[]} <i>and</i>
 * uploads the same bytes to OpenAL, so every sound costs double. Nothing reads the heap copy afterwards, so it is
 * released here - see {@link #loadSound(FilenameURL)}.</li>
 * <li><b>Stereo sounds cannot be positioned.</b> OpenAL skips its 3D pipeline for multi-channel buffers unless asked
 * not to. {@link #play(Source)} sets AL_SOURCE_SPATIALIZE_SOFT per source, which lets stereo sounds keep their width
 * instead of being downmixed - see {@link SpatializeSupport}.</li>
 * </ul>
 */
public class LibraryHodgepodgeOpenAL extends LibraryLWJGLOpenAL {

    public LibraryHodgepodgeOpenAL() throws SoundSystemException {
        super();
        // A reload builds a new Library on a new AL context, so anything we cached about the old one is stale.
        // This is the reliable signal: a recreated device can land on the same native pointer, so comparing handles
        // is not enough. Doing it here rather than off a device-change check also keeps it working on Java 8.
        ReverbSupport.invalidate();
        SoundDeviceTweaks.invalidate();
    }

    /**
     * Registers this in place of the stock library. Minecraft adds {@link LibraryLWJGLOpenAL} in the SoundManager
     * constructor and then fires SoundSetupEvent, and SoundSystem's no-arg constructor walks the registry in order
     * using the first entry that initialises - so removing theirs and adding ours wins every time, including for the
     * SoundSystems ArchaicFix recreates on a device change.
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
     * {@code loadSound} uses its presence as the "already decoded" check - only the bytes are redundant. Safe because
     * the only reader of {@code audioData} after upload is streaming pre-load, and streaming sources never come through
     * here.
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
     * Configures the source once it has a channel; Paulscode attaches it inside super.play().
     * <p>
     * The {@code attachedSource} check is not optional. {@code play()} can return without assigning a channel - the
     * source may be inactive, already playing, or every channel busy - while {@code source.channel} still points at a
     * channel {@code getNextChannel} has since handed to a different source, since it reassigns without clearing the
     * previous owner's reference. Paulscode guards every one of its own AL accesses the same way.
     */
    @Override
    public void play(Source source) {
        super.play(source);
        if (source == null || !(source.channel instanceof ChannelLWJGLOpenAL channel)) return;
        if (channel.attachedSource != source || channel.ALSource == null) return;

        final int alSource = channel.ALSource.get(0);
        // Channels are pooled, so both of these must be written on every attach, not just when switching on -
        // otherwise a UI click inherits the settings of whatever world sound used the channel last.
        final boolean positional = source.attModel != SoundSystemConfig.ATTENUATION_NONE;
        SpatializeSupport.apply(alSource, positional);
        ReverbSupport.route(alSource, positional);
    }
}
