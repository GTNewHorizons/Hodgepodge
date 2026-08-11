package com.mitchej123.hodgepodge.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.mitchej123.hodgepodge.Common;

import paulscode.sound.SoundSystemConfig;

@Config(modid = "hodgepodge", category = "sound")
public class SoundConfig {

    public enum AttenuationType {
        ATTENUATION_NONE,
        ATTENUATION_ROLLOFF,
        ATTENUATION_LINEAR

    }

    @Config.Comment({ "Maximum number of normal (non-streaming) channels available for simultaneous sound effects.",
            "OpenAL Soft defaults to 256 sources unless overridden. If fewer are available, Paulscode creates as many channels as it can.",
            "Takes effect after 'Reload Sounds' in the sound options, or a restart." })
    @Config.DefaultInt(64)
    public static int numberNormalChannels;

    @Config.Comment({ "Maximum number of streaming channels: music, records, and other streamed audio playing at once.",
            "Takes effect after 'Reload Sounds' in the sound options, or a restart." })
    @Config.DefaultInt(8)
    public static int numberStreamingChannels;

    @Config.Comment({
            "Attenuation model to use if not specified. Attenuation is how a source's volume fades with distance.",
            "ATTENUATION_NONE: Global identifier for no attenuation. Attenuation is how a source's volume fades with distance. When there is no attenuation, a source's volume remains constant regardless of distance.",
            "ATTENUATION_ROLLOFF: Global identifier for rolloff attenuation. Rolloff attenuation is a realistic attenuation model, which uses a rolloff factor to determine how quickly a source fades with distance. A smaller rolloff factor will fade at a further distance, and a rolloff factor of 0 will never fade. NOTE: In OpenAL, rolloff attenuation only works for monotone sounds.",
            "ATTENUATION_LINEAR: Global identifier for linear attenuation. Linear attenuation is less realistic than rolloff attenuation, but it allows the user to specify a maximum \"fade distance\" where a source's volume becomes zero." })
    @Config.DefaultEnum("ATTENUATION_ROLLOFF")
    public static AttenuationType defaultAttenuationModel;

    @Config.Comment("Default value to use for the rolloff factor if not specified.")
    @Config.DefaultFloat(0.03f)
    public static float defaultRolloffFactor;

    @Config.Comment("Value to use for the Doppler factor, for determining Doppler scale.")
    @Config.DefaultFloat(0.0f)
    public static float dopplerFactor;

    @Config.Comment("Value to use for the Doppler velocity.")
    @Config.DefaultFloat(1.0f)
    public static float dopplerVelocity;

    @Config.Comment("Default value to use for fade distance if not specified.")
    @Config.DefaultFloat(1000.0f)
    public static float defaultFadeDistance;

    @Config.Comment("Number of bytes to load at a time when streaming.")
    @Config.DefaultInt(131072)
    public static int streamingBufferSize;

    @Config.Comment("Number of buffers used for each streaming source. Slow codecs may require this number to be greater than 2 to prevent audio skipping during playback.")
    @Config.DefaultInt(3)
    public static int numberStreamingBuffers;

    @Config.Comment({
            "Enables a transition-speed optimization by assuming all sounds in each streaming source's queue will have exactly the same format once decoded (including channels, sample rate, and sample size). This is an advanced setting which should only be changed by experienced developers.",
            "NOTE: I have not checked if this is even true for vanilla. Changing this setting will most likely break things." })
    @Config.DefaultBoolean(false)
    public static boolean streamQueueFormatsMatch;

    @Config.Comment("Maximum decoded size of a non-streaming sound. OGG decoding stops at this limit on a complete PCM frame. Streamed sounds are unaffected.")
    @Config.DefaultInt(268435456)
    public static int maxFileSize;

    @Config.Comment("Size of each chunk used by non-streaming codecs that honor it. OGG uses streamingBufferSize to keep decode copying bounded.")
    @Config.DefaultInt(1048576)
    public static int fileChunkSize;

    @Config.Comment("MIDI device to try using as the Synthesizer. May be the full name or part of the name. If this String is empty, the default Synthesizer will be used, or one of the common alternate synthesizers if the default Synthesizer is unavailable.")
    @Config.DefaultString("")
    public static String overrideMIDISynthesizer;

    @Config.Comment({
            "Downmix non-streaming stereo OGG sounds to mono, halving their decoded PCM size and allowing positional audio.",
            "Non-streaming is used as a proxy for positional, so rare non-positional effects may also be downmixed. Streaming sounds, normally music and records, are unaffected.",
            "Ignored for sounds handled by spatializeStereoSounds. Takes full effect after 'Reload Sounds' or a restart." })
    @Config.DefaultBoolean(true)
    public static boolean downmixStereoSounds;

    @Config.Comment({
            "Let OpenAL position stereo sounds that use distance attenuation without converting them to mono. This preserves the stereo PCM but uses roughly twice the buffer memory of mono.",
            "Requires lwjgl3ify and AL_SOFT_source_spatialize; otherwise downmixStereoSounds can provide the fallback.",
            "Takes full effect after 'Reload Sounds' or a restart." })
    @Config.DefaultBoolean(true)
    public static boolean spatializeStereoSounds;

    @Config.Comment({
            "Sounds whose path contains any of these are never downmixed, so interface sounds keep their stereo.",
            "Matched case-insensitively against 'domain:path', e.g. 'gregtech:sounds/buttonup.ogg'.",
            "Only used by downmixStereoSounds. spatializeStereoSounds needs no list because it decides per playback from the attenuation model.",
            "Broad patterns can also match world sounds; those stay stereo and cannot be positioned by the downmix fallback." })
    @Config.DefaultStringList({ "button", "click", "/gui", "menu", "typing", "page" })
    public static String[] downmixExclusions;

    @Config.Comment({
            "Discard the Java-heap PCM copy after a non-streaming sound is uploaded to OpenAL, removing one of the two cached decoded copies.",
            "Turn off only if you suspect it of causing missing or corrupted sounds. Takes full effect after 'Reload Sounds' or a restart." })
    @Config.DefaultBoolean(true)
    public static boolean releaseDecodedSoundData;

    @Config.Comment({
            "Add environmental reverb to positional sounds based on how enclosed the listener is. Requires OpenAL EFX, available in the bundled Java 8 and lwjgl3ify backends.",
            "Surroundings are estimated by sampling 12 directions up to 20 blocks four times per second.",
            "Takes full effect after 'Reload Sounds' or a restart." })
    @Config.DefaultBoolean(false)
    public static boolean environmentalReverb;

    @Config.Comment({ "How wet the reverb gets in a fully enclosed space. Lower is subtler.",
            "Only used when environmentalReverb is on." })
    @Config.DefaultFloat(0.3f)
    @Config.RangeFloat(min = 0.0f, max = 1.0f)
    public static float reverbStrength;

    public enum Tristate {
        DEFAULT,
        ON,
        OFF
    }

    @Config.Comment({
            "Binaural 3D audio for headphones (HRTF): lets you hear whether a sound is above, below, in front or behind you, instead of just left/right.",
            "Designed for headphones; speakers may sound hollow or coloured. DEFAULT leaves it to OpenAL's device configuration, ON forces it, OFF forces it off.",
            "Requires lwjgl3ify; ignored on Java 8." })
    @Config.DefaultEnum("DEFAULT")
    public static Tristate hrtf;

    @Config.Comment({
            "Protects the final output mix from clipping by reducing gain when its combined level exceeds the device range.",
            "It reacts to signal level, not the number of playing sounds, and does not limit sound or channel count.",
            "DEFAULT leaves it to OpenAL, ON forces it, OFF disables it. Requires lwjgl3ify; ignored on Java 8." })
    @Config.DefaultEnum("DEFAULT")
    public static Tristate outputLimiter;

    public static void apply() {
        SoundSystemConfig.setNumberNormalChannels(numberNormalChannels);
        SoundSystemConfig.setNumberStreamingChannels(numberStreamingChannels);
        SoundSystemConfig.setDefaultAttenuation(defaultAttenuationModel.ordinal());
        SoundSystemConfig.setDefaultRolloff(defaultRolloffFactor);
        SoundSystemConfig.setDopplerFactor(dopplerFactor);
        SoundSystemConfig.setDopplerVelocity(dopplerVelocity);
        SoundSystemConfig.setDefaultFadeDistance(defaultFadeDistance);
        SoundSystemConfig.setStreamingBufferSize(streamingBufferSize);
        SoundSystemConfig.setNumberStreamingBuffers(numberStreamingBuffers);
        SoundSystemConfig.setStreamQueueFormatsMatch(streamQueueFormatsMatch);
        SoundSystemConfig.setMaxFileSize(maxFileSize);
        SoundSystemConfig.setFileChunkSize(fileChunkSize);
        SoundSystemConfig.setOverrideMIDISynthesizer(overrideMIDISynthesizer);
        Common.log.warn("Sound Config Applied");
    }
}
