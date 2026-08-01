package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.config.SoundConfig;

/**
 * Environmental reverb, so caves and enclosed rooms sound like caves and enclosed rooms.
 * <p>
 * Uses ALC_EXT_EFX, which is present on <b>both</b> tiers - LWJGL2 bundles the bindings as {@code EFX10} and LWJGL3 as
 * {@code EXTEfx}, with identical method names and constants - so unlike HRTF this is not lwjgl3ify-only. Reflective for
 * the usual reason: both versions live in {@code org.lwjgl.openal}, so they cannot both be on the compile classpath.
 * <p>
 * The room is estimated by casting a handful of short rays from the player and looking at how far they travel before
 * hitting something. That is deliberately cheap and approximate; it is a tuning knob, not physics.
 */
public final class ReverbSupport {

    private ReverbSupport() {}

    private static final int AL_EFFECT_TYPE = 32769;
    private static final int AL_EFFECT_REVERB = 1;
    private static final int AL_EFFECTSLOT_EFFECT = 1;
    private static final int AL_AUXILIARY_SEND_FILTER = 131078;
    private static final int AL_FILTER_NULL = 0;
    private static final int AL_REVERB_DENSITY = 1;
    private static final int AL_REVERB_DIFFUSION = 2;
    private static final int AL_REVERB_GAIN = 3;
    private static final int AL_REVERB_GAINHF = 4;
    private static final int AL_REVERB_DECAY_TIME = 5;
    private static final int AL_REVERB_REFLECTIONS_GAIN = 7;
    private static final int AL_REVERB_LATE_REVERB_GAIN = 9;

    /** Rays cast per probe, and how far each travels. 12 x 20 block lookups a few times a second is negligible. */
    private static final int RAYS = 12;
    private static final int RAY_LENGTH = 20;
    /**
     * Spread of the ray fan, as a fraction of a full sphere. 1.2 puts elevations between about +72 and -9 degrees:
     * mostly upward, dipping just below the horizon.
     * <p>
     * A full sphere does not work. Standing outdoors, every downward ray hits the ground immediately, which drags the
     * score down until open ground and a large cavern look the same. Weighting upward separates them - simulated over
     * flat ground, a hut, a corridor and a cavern, 1.2 gave the widest spread of scores.
     */
    private static final double RAY_SPREAD = 1.2;

    private static Method alEffecti, alEffectf, alAuxiliaryEffectSloti, alSource3i;
    private static int effect = 0, slot = 0;
    private static boolean resolved = false;
    private static boolean supported = false;
    private static float appliedDecay = -1f;

    public static boolean active() {
        return SoundConfig.environmentalReverb && resolve();
    }

    /** Routes a source through the reverb slot. Called per play, since channels are pooled. */
    static void route(int sourceId) {
        if (!active()) return;
        try {
            alSource3i.invoke(null, sourceId, AL_AUXILIARY_SEND_FILTER, slot, 0, AL_FILTER_NULL);
        } catch (Throwable t) {
            supported = false;
            Common.log.warn("Could not route a sound through reverb, disabling it", t);
        }
    }

    /** Re-probes the surroundings and retunes the reverb. Polled from the client tick. */
    public static void tick() {
        if (!SoundConfig.environmentalReverb) return;
        final Minecraft mc = Minecraft.getMinecraft();
        final EntityPlayer player = mc.thePlayer;
        // Being in a world guarantees the sound system finished starting, so resolve() below has a live AL context
        // to create its effect objects in.
        if (player == null || mc.theWorld == null) return;
        if (!resolve()) return;
        final float[] room = probeRoom(mc.theWorld, player);
        applyRoom(room[0], room[1]);
    }

    /**
     * Returns {@code { openness, meanDistance }} - how much space surrounds the player (0 sealed, 1 open sky) and how
     * far a ray gets on average, which stands in for room size.
     * <p>
     * Stepped a block at a time rather than via rayTrace, to avoid allocating vectors several times a second. Rooms
     * bigger than {@link #RAY_LENGTH} read as open sky; that is the accepted ceiling of a cheap probe.
     */
    private static float[] probeRoom(World world, EntityPlayer player) {
        final double px = player.posX, py = player.posY, pz = player.posZ;
        int totalSteps = 0;
        int totalDistance = 0;
        for (int ray = 0; ray < RAYS; ray++) {
            final double theta = Math.PI * 2.0 * ray / RAYS;
            final double phi = Math.acos(1.0 - RAY_SPREAD * (ray + 0.5) / RAYS);
            final double dx = Math.sin(phi) * Math.cos(theta);
            final double dy = Math.cos(phi);
            final double dz = Math.sin(phi) * Math.sin(theta);
            int distance = RAY_LENGTH;
            for (int step = 1; step <= RAY_LENGTH; step++) {
                final int bx = (int) Math.floor(px + dx * step);
                final int by = (int) Math.floor(py + dy * step);
                final int bz = (int) Math.floor(pz + dz * step);
                if (by < 0 || by > 255) break; // out of the world counts as open
                if (world.getBlock(bx, by, bz).isOpaqueCube()) {
                    distance = step;
                    break;
                }
                totalSteps++;
            }
            totalDistance += distance;
        }
        return new float[] { totalSteps / (float) (RAYS * RAY_LENGTH), totalDistance / (float) RAYS };
    }

    /**
     * Size sets the decay, enclosure sets how much of it you hear. Keeping those separate is what stops a broom
     * cupboard ringing like a cathedral: a cavern gets long but subtle reverb, a small room short but obvious, and open
     * ground effectively none.
     */
    private static void applyRoom(float openness, float meanDistance) {
        final float enclosure = 1f - openness;
        final float decay = 0.15f + (meanDistance / RAY_LENGTH) * enclosure * 3.0f;
        final float wet = enclosure * enclosure * SoundConfig.reverbStrength;
        if (Math.abs(decay - appliedDecay) < 0.05f) return; // avoid churning the effect every tick
        appliedDecay = decay;
        try {
            alEffectf.invoke(null, effect, AL_REVERB_DECAY_TIME, clamp(decay, 0.1f, 20f));
            alEffectf.invoke(null, effect, AL_REVERB_GAIN, clamp(wet, 0f, 1f));
            alEffectf.invoke(null, effect, AL_REVERB_LATE_REVERB_GAIN, clamp(wet * 1.5f, 0f, 10f));
            alEffectf.invoke(null, effect, AL_REVERB_REFLECTIONS_GAIN, clamp(wet, 0f, 3.16f));
            alEffectf.invoke(null, effect, AL_REVERB_DENSITY, clamp(enclosure, 0f, 1f));
            alEffectf.invoke(null, effect, AL_REVERB_DIFFUSION, clamp(0.6f + enclosure * 0.4f, 0f, 1f));
            alEffectf.invoke(null, effect, AL_REVERB_GAINHF, clamp(1f - enclosure * 0.4f, 0f, 1f));
            // Re-attaching is what makes the new values take effect on the slot.
            alAuxiliaryEffectSloti.invoke(null, slot, AL_EFFECTSLOT_EFFECT, effect);
        } catch (Throwable t) {
            supported = false;
            Common.log.warn("Could not update reverb, disabling it", t);
        }
    }

    private static float clamp(float v, float lo, float hi) {
        return v < lo ? lo : v > hi ? hi : v;
    }

    /** Called when a new Library is built, since the effect and slot belonged to the previous AL context. */
    static synchronized void invalidate() {
        resolved = false;
        supported = false;
        effect = 0;
        slot = 0;
        appliedDecay = -1f;
    }

    private static synchronized boolean resolve() {
        if (resolved) return supported;
        resolved = true;
        try {
            final Class<?> efx = efxClass();
            if (efx == null) return false;
            final Class<?> al11 = Class.forName("org.lwjgl.openal.AL11");
            alSource3i = al11.getMethod("alSource3i", int.class, int.class, int.class, int.class, int.class);
            alEffecti = efx.getMethod("alEffecti", int.class, int.class, int.class);
            alEffectf = efx.getMethod("alEffectf", int.class, int.class, float.class);
            alAuxiliaryEffectSloti = efx.getMethod("alAuxiliaryEffectSloti", int.class, int.class, int.class);

            effect = (Integer) efx.getMethod("alGenEffects").invoke(null);
            slot = (Integer) efx.getMethod("alGenAuxiliaryEffectSlots").invoke(null);
            alEffecti.invoke(null, effect, AL_EFFECT_TYPE, AL_EFFECT_REVERB);
            supported = true;
            Common.log.info("Environmental reverb enabled (EFX effect {}, slot {})", effect, slot);
        } catch (Throwable t) {
            Common.log.warn("EFX reverb unavailable, leaving audio dry", t);
        }
        return supported;
    }

    /** LWJGL3 calls it EXTEfx, LWJGL2 calls it EFX10; the methods and constants are the same. */
    private static Class<?> efxClass() {
        for (final String name : new String[] { "org.lwjgl.openal.EXTEfx", "org.lwjgl.openal.EFX10" }) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException ignored) {}
        }
        Common.log.info("No EFX bindings found, skipping reverb");
        return null;
    }
}
