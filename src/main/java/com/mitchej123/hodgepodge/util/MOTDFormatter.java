package com.mitchej123.hodgepodge.util;

import java.text.DecimalFormat;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

import com.mitchej123.hodgepodge.config.TweaksConfig;

public class MOTDFormatter {

    private static final DecimalFormat TPS_FORMAT = new DecimalFormat("0.0");
    private static final DecimalFormat MEMORY_FORMAT = new DecimalFormat("0");

    private static long serverStartTime = 0L;

    public static IChatComponent buildMOTD(MinecraftServer server) {
        if (serverStartTime == 0L) {
            serverStartTime = System.currentTimeMillis();
        }

        if (!TweaksConfig.customMotdEnabled) {
            return new ChatComponentText(server.getMOTD());
        }

        String line1 = processVariables(TweaksConfig.motdLine1, server);
        String line2 = processVariables(TweaksConfig.motdLine2, server);

        String combinedText = line1 + "\n" + line2;

        return new ChatComponentText(combinedText);
    }

    private static String processVariables(String text, MinecraftServer server) {
        String result = text;

        result = result.replace("{players}", String.valueOf(server.getCurrentPlayerCount()));

        result = result.replace("{maxPlayers}", String.valueOf(server.getMaxPlayers()));

        if (result.contains("{tps}")) {
            result = result.replace("{tps}", calculateTPS(server));
        }

        if (result.contains("{memory}")) {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory() / 1024 / 1024; // Convert to MB
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            long usedMemory = totalMemory - freeMemory;

            result = result.replace(
                    "{memory}",
                    MEMORY_FORMAT.format(usedMemory) + "/" + MEMORY_FORMAT.format(maxMemory) + "MB");
        }

        if (result.contains("{uptime}")) {
            long uptimeMillis = System.currentTimeMillis() - serverStartTime;
            result = result.replace("{uptime}", formatUptime(uptimeMillis));
        }

        return result;
    }

    private static String calculateTPS(MinecraftServer server) {
        try {
            // Calculate average tick time from tickTimeArray (public field)
            long[] tickTimes = server.tickTimeArray;

            // Calculate average tick time in nanoseconds
            double avgTickTimeNanos = MathHelper.average(tickTimes);

            // Convert to milliseconds
            double avgTickTimeMs = avgTickTimeNanos * 1.0E-6D;

            // Calculate TPS (1000ms per second / ms per tick)
            // Cap at 20.0 TPS maximum
            double tps = Math.min(20.0, 1000.0 / avgTickTimeMs);

            return TPS_FORMAT.format(tps);
        } catch (Exception e) {
            // Fallback if calculation fails
            return "20.0";
        }
    }

    private static String formatUptime(long uptimeMillis) {
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m";
        } else {
            return seconds + "s";
        }
    }
}
