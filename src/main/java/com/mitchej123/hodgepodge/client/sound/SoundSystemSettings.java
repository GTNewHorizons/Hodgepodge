package com.mitchej123.hodgepodge.client.sound;

import java.util.List;

import com.mitchej123.hodgepodge.config.SoundConfig;

import paulscode.sound.SoundSystemConfig;

/** The SoundSystemConfig settings Hodgepodge owns. */
public final class SoundSystemSettings {

    private SoundSystemSettings() {}

    public static void apply() {
        each(null);
    }

    static void snapshot(List<Object> out) {
        each(out);
    }

    // out == null writes our config, otherwise records the current values.
    // One row per setting, so what we apply and what the boot snapshot compares cannot drift.
    private static void each(List<Object> out) {
        final boolean apply = out == null;

        if (apply) SoundSystemConfig.setNumberNormalChannels(
                ChannelFloor.NORMAL
                        .resolve(SoundConfig.numberNormalChannels, SoundSystemConfig.getNumberNormalChannels()));
        else out.add(SoundSystemConfig.getNumberNormalChannels());

        if (apply) SoundSystemConfig.setNumberStreamingChannels(
                ChannelFloor.STREAMING
                        .resolve(SoundConfig.numberStreamingChannels, SoundSystemConfig.getNumberStreamingChannels()));
        else out.add(SoundSystemConfig.getNumberStreamingChannels());

        if (apply) SoundSystemConfig.setDefaultAttenuation(SoundConfig.defaultAttenuationModel.ordinal());
        else out.add(SoundSystemConfig.getDefaultAttenuation());

        if (apply) SoundSystemConfig.setDefaultRolloff(SoundConfig.defaultRolloffFactor);
        else out.add(SoundSystemConfig.getDefaultRolloff());

        if (apply) SoundSystemConfig.setDopplerFactor(SoundConfig.dopplerFactor);
        else out.add(SoundSystemConfig.getDopplerFactor());

        if (apply) SoundSystemConfig.setDopplerVelocity(SoundConfig.dopplerVelocity);
        else out.add(SoundSystemConfig.getDopplerVelocity());

        if (apply) SoundSystemConfig.setDefaultFadeDistance(SoundConfig.defaultFadeDistance);
        else out.add(SoundSystemConfig.getDefaultFadeDistance());

        if (apply) SoundSystemConfig.setStreamingBufferSize(SoundConfig.streamingBufferSize);
        else out.add(SoundSystemConfig.getStreamingBufferSize());

        if (apply) SoundSystemConfig.setNumberStreamingBuffers(SoundConfig.numberStreamingBuffers);
        else out.add(SoundSystemConfig.getNumberStreamingBuffers());

        if (apply) SoundSystemConfig.setStreamQueueFormatsMatch(SoundConfig.streamQueueFormatsMatch);
        else out.add(SoundSystemConfig.getStreamQueueFormatsMatch());

        if (apply) SoundSystemConfig.setMaxFileSize(SoundConfig.maxFileSize);
        else out.add(SoundSystemConfig.getMaxFileSize());

        if (apply) SoundSystemConfig.setFileChunkSize(SoundConfig.fileChunkSize);
        else out.add(SoundSystemConfig.getFileChunkSize());

        if (apply) SoundSystemConfig.setOverrideMIDISynthesizer(SoundConfig.overrideMIDISynthesizer);
        else out.add(SoundSystemConfig.getOverrideMIDISynthesizer());
    }
}
