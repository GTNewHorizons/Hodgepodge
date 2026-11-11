package com.mitchej123.hodgepodge.client.sound;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.Compat;
import com.mitchej123.hodgepodge.config.SoundConfig;

/** Selects and recovers OpenAL Soft output devices without rebuilding the sound system. */
public final class OutputDeviceSupport {

    public static final String SYSTEM_DEFAULT = "";

    private static final int ALC_DEFAULT_ALL_DEVICES_SPECIFIER = 0x1012;
    private static final int ALC_ALL_DEVICES_SPECIFIER = 0x1013;
    private static final int ALC_CONNECTED = 0x313;
    private static final String OPENAL_SOFT_DEVICE_PREFIX = "OpenAL Soft on ";
    private static final long POLL_INTERVAL_NANOS = 1_000_000_000L;
    private static final long ENUMERATION_INTERVAL_NANOS = 5_000_000_000L;
    private static final long RETRY_INTERVAL_NANOS = 5_000_000_000L;

    private static final AtomicInteger reloads = new AtomicInteger();
    private static int seenReload;
    private static Method isCreated;
    private static Method getDevice;
    private static Field deviceHandle;
    private static Method isExtensionPresent;
    private static Method getString;
    private static Method getStringPointer;
    private static Method getInteger;
    private static Method reopenDevice;
    private static Method readByte;
    private static Method readUtf8;
    private static boolean methodsResolved;
    private static boolean warned;
    private static String activeTarget;
    private static String activeSystemDefault;
    private static List<String> cachedDevices;
    private static boolean deviceEnumerationValid;
    private static long nextPoll;
    private static long nextEnumeration;
    private static long nextRetry;

    private OutputDeviceSupport() {}

    static void invalidate() {
        reloads.incrementAndGet();
    }

    public static boolean takesOwnership() {
        return SoundConfig.manageOutputDevicesAtStartup && available();
    }

    public static boolean available() {
        if (!SoundConfig.manageOutputDevicesAtStartup || !Compat.isLwjgl3ifyPresent()) return false;
        try {
            resolveMethods();
            long device = currentDevice();
            return device != 0L && (Boolean) isExtensionPresent.invoke(null, device, "ALC_SOFT_reopen_device");
        } catch (Throwable t) {
            warnOnce("Could not access OpenAL output devices", t);
            return false;
        }
    }

    /** Returns current playback endpoints. The empty string represents the system default. */
    public static List<String> devices() {
        long now = System.nanoTime();
        if (cachedDevices != null && nextEnumeration != 0L && now - nextEnumeration < 0L) {
            return new ArrayList<>(cachedDevices);
        }

        List<String> devices = new ArrayList<>();
        devices.add(SYSTEM_DEFAULT);
        if (!available()) return enumerationFailed(now, devices);
        try {
            long address = (Long) getStringPointer.invoke(null, 0L, ALC_ALL_DEVICES_SPECIFIER);
            if (address == 0L) throw new IllegalStateException("OpenAL returned no output devices");
            while (address != 0L && (Byte) readByte.invoke(null, address) != 0) {
                String name = (String) readUtf8.invoke(null, address);
                devices.add(name);
                address += name.getBytes(StandardCharsets.UTF_8).length + 1L;
            }
            if (devices.size() == 1) throw new IllegalStateException("OpenAL returned no output devices");
        } catch (Throwable t) {
            warnOnce("Could not enumerate OpenAL output devices", t);
            return enumerationFailed(now, devices);
        }
        cachedDevices = devices;
        deviceEnumerationValid = true;
        nextEnumeration = now + ENUMERATION_INTERVAL_NANOS;
        return new ArrayList<>(devices);
    }

    /** Switches immediately. The caller should save the preference only when this succeeds. */
    public static boolean select(String requestedDevice) {
        String requested = requestedDevice == null ? SYSTEM_DEFAULT : requestedDevice;
        if (!available()) return false;
        try {
            return reopen(requested);
        } catch (Throwable t) {
            Common.log.warn("Could not switch OpenAL output device to {}", displayName(requested), t);
            return false;
        }
    }

    /** Polls because system event callbacks are not guaranteed on every OpenAL backend. Client thread only. */
    public static void tick() {
        if (!SoundConfig.manageOutputDevicesAtStartup || !Compat.isLwjgl3ifyPresent()) return;
        long now = System.nanoTime();
        if (nextPoll != 0L && now - nextPoll < 0L) return;
        nextPoll = now + POLL_INTERVAL_NANOS;
        try {
            consumeInvalidate();
            if (!available()) return;

            long device = currentDevice();
            boolean connected = isConnected(device);
            String desired = SoundConfig.outputDevice == null ? SYSTEM_DEFAULT : SoundConfig.outputDevice;
            List<String> devices = devices();
            String current = string(device, ALC_ALL_DEVICES_SPECIFIER);
            boolean desiredAvailable = deviceEnumerationValid ? devices.contains(desired)
                    : connected && desired.equals(current);
            String target = desired.isEmpty() || desiredAvailable ? desired : SYSTEM_DEFAULT;
            String systemDefault = string(0L, ALC_DEFAULT_ALL_DEVICES_SPECIFIER);

            if (activeTarget == null && connected) {
                if (target.isEmpty()) {
                    if (current.equals(systemDefault)) {
                        activeTarget = SYSTEM_DEFAULT;
                        activeSystemDefault = systemDefault;
                    }
                } else if (current.equals(target)) {
                    activeTarget = target;
                }
            }

            boolean needsSwitch = !connected || activeTarget != null && !activeTarget.equals(target);
            if (activeTarget == null) needsSwitch |= !current.equals(target.isEmpty() ? systemDefault : target);
            if (target.isEmpty() && activeSystemDefault != null) {
                needsSwitch |= !activeSystemDefault.equals(systemDefault);
            }

            if (needsSwitch && (nextRetry == 0L || now - nextRetry >= 0L)) {
                if (!reopen(target)) nextRetry = now + RETRY_INTERVAL_NANOS;
            }
        } catch (Throwable t) {
            warnOnce("Could not monitor the OpenAL output device", t);
        }
    }

    public static String displayName(String device) {
        if (device == null || device.isEmpty()) return "System Default";
        return device.startsWith(OPENAL_SOFT_DEVICE_PREFIX) ? device.substring(OPENAL_SOFT_DEVICE_PREFIX.length())
                : device;
    }

    private static boolean reopen(String requested) throws Exception {
        long device = currentDevice();
        String argument = requested.isEmpty() ? null : requested;
        boolean ok = (Boolean) reopenDevice.invoke(null, device, argument, null);
        if (!ok) {
            Common.log.warn("OpenAL refused output device {}", displayName(requested));
            return false;
        }
        if (!isConnected(device)) {
            Common.log.warn("OpenAL output device {} opened but failed to start", displayName(requested));
            return false;
        }
        String activeDevice = string(device, ALC_ALL_DEVICES_SPECIFIER);
        activeTarget = requested;
        activeSystemDefault = requested.isEmpty() ? string(0L, ALC_DEFAULT_ALL_DEVICES_SPECIFIER) : null;
        nextRetry = 0L;
        SoundDeviceTweaks.invalidate();
        Common.log.info("OpenAL output device: {}", activeDevice);
        return true;
    }

    private static void consumeInvalidate() {
        int current = reloads.get();
        if (current == seenReload) return;
        seenReload = current;
        activeTarget = null;
        activeSystemDefault = null;
        cachedDevices = null;
        deviceEnumerationValid = false;
        nextRetry = 0L;
        nextEnumeration = 0L;
        warned = false;
    }

    private static boolean isConnected(long device) throws Exception {
        int[] connected = { 1 };
        getInteger.invoke(null, device, ALC_CONNECTED, connected);
        return connected[0] != 0;
    }

    private static long currentDevice() throws Exception {
        if (!(Boolean) isCreated.invoke(null)) return 0L;
        Object device = getDevice.invoke(null);
        if (device == null) return 0L;
        return deviceHandle.getLong(device);
    }

    private static String string(long device, int name) throws Exception {
        String value = (String) getString.invoke(null, device, name);
        return value == null ? "" : value;
    }

    private static List<String> enumerationFailed(long now, List<String> fallback) {
        deviceEnumerationValid = false;
        nextEnumeration = now + RETRY_INTERVAL_NANOS;
        if (cachedDevices == null) cachedDevices = fallback;
        return new ArrayList<>(cachedDevices);
    }

    private static synchronized void resolveMethods() throws Exception {
        if (methodsResolved) return;
        Class<?> al = Class.forName("org.lwjglx.openal.AL");
        Class<?> alc10 = Class.forName("org.lwjgl.openal.ALC10");
        Class<?> memory = Class.forName("org.lwjgl.system.MemoryUtil");
        isCreated = al.getMethod("isCreated");
        getDevice = al.getMethod("getDevice");
        deviceHandle = Class.forName("org.lwjglx.openal.ALCdevice").getField("device");
        isExtensionPresent = alc10.getMethod("alcIsExtensionPresent", long.class, CharSequence.class);
        getString = alc10.getMethod("alcGetString", long.class, int.class);
        getStringPointer = alc10.getMethod("nalcGetString", long.class, int.class);
        getInteger = alc10.getMethod("alcGetIntegerv", long.class, int.class, int[].class);
        reopenDevice = Class.forName("org.lwjgl.openal.SOFTReopenDevice")
                .getMethod("alcReopenDeviceSOFT", long.class, CharSequence.class, int[].class);
        readByte = memory.getMethod("memGetByte", long.class);
        readUtf8 = memory.getMethod("memUTF8", long.class);
        methodsResolved = true;
    }

    private static void warnOnce(String message, Throwable t) {
        if (warned) return;
        warned = true;
        Common.log.warn(message + "; leaving output unchanged", t);
    }

}
