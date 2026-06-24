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

    @Config.Comment("Maximum number of normal (non-streaming) channels that can be created.")
    @Config.DefaultInt(28)
    public static int numberNormalChannels;

    @Config.Comment("Maximum number of streaming channels that can be created.")
    @Config.DefaultInt(4)
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

    @Config.Comment("Value to use for the doppler factor, for determining Doppler scale.")
    @Config.DefaultFloat(0.0f)
    public static float dopplerFactor;

    @Config.Comment("Value to use for the doppler velocity.")
    @Config.DefaultFloat(1.0f)
    public static float dopplerVelocity;

    @Config.Comment("Default value to use for fade distance if not specified.")
    @Config.DefaultFloat(1000.0f)
    public static float defaultFadeDistance;

    @Config.Comment("Number of bytes to load at a time when streaming.")
    @Config.DefaultInt(131072)
    public static int streamingBufferSize;

    @Config.Comment("Number of buffers used for each streaming sorce. Slow codecs may require this number to be greater than 2 to prevent audio skipping during playback.")
    @Config.DefaultInt(3)
    public static int numberStreamingBuffers;

    @Config.Comment({
            "Enables a transition-speed optimization by assuming all sounds in each streaming source's queue will have exactly the same format once decoded (including channels, sample rate, and sample size). This is an advanced setting which should only be changed by experienced developers.",
            "NOTE: I have not checked if this is even true for vanilla. Changing this setting will most likely break things." })
    @Config.DefaultBoolean(false)
    public static boolean streamQueueFormatsMatch;

    @Config.Comment("The maximum number of bytes to read in for (non-streaming) files. Increase this value if non-streaming sounds are getting cut off. Decrease this value if large sound files are causing lag during load time.")
    @Config.DefaultInt(268435456)
    public static int maxFileSize;

    @Config.Comment("Size of each chunk to read at a time for loading (non-streaming) files. Increase if loading sound files is causing significant lag.")
    @Config.DefaultInt(1048576)
    public static int fileChunkSize;

    @Config.Comment("MIDI device to try using as the Synthesizer. May be the full name or part of the name. If this String is empty, the default Synthesizer will be used, or one of the common alternate synthesizers if the default Synthesizer is unavailable.")
    @Config.DefaultString("")
    public static String overrideMIDISynthesizer;

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
