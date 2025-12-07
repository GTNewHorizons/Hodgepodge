package com.mitchej123.hodgepodge.client.biomesoplenty;

import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;

import com.mitchej123.hodgepodge.config.ASMConfig;
import com.mitchej123.hodgepodge.mixins.late.biomesoplenty.AccessorFogHandler;

import biomesoplenty.client.fog.FogHandler;
import biomesoplenty.client.fog.IBiomeFog;

public class BOPFogHandler {

    private static AtomicInteger ticks = new AtomicInteger(-100);

    private static float farPlaneDistanceScale = 0.75f;
    private static float farPlaneDistanceM;

    public static void onRenderFog(EntityViewRenderEvent.RenderFogEvent event, FogHandler fogHandler) {
        if (ASMConfig.bopFogDisable) return;

        EntityLivingBase entity = event.entity;
        int playerX = MathHelper.floor_double(entity.posX);
        int playerZ = MathHelper.floor_double(entity.posZ);

        if (playerX == AccessorFogHandler.getFogX() && playerZ == AccessorFogHandler.getFogZ()
                && AccessorFogHandler.isFogInit())
            AccessorFogHandler.callRenderFog(event.fogMode, AccessorFogHandler.getFogFarPlaneDistance(), 0.75f);

        farPlaneDistanceM = event.farPlaneDistance;

        if (ticks.get() < -50) {
            new ClientTickThread().start();
            ticks.set(0);
        }

        if (Math.random() < 0.1) ticks.incrementAndGet();

        AccessorFogHandler
                .callRenderFog(event.fogMode, AccessorFogHandler.getFogFarPlaneDistance(), farPlaneDistanceScale);
    }

    private static class ClientTickThread extends Thread {

        @Override
        public void run() {
            while (true) {
                if (Minecraft.getMinecraft().theWorld == null) {
                    ticks.set(0);
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (ticks.get() > 0) {
                    try {
                        int distance = 20;
                        float fpDistanceBiomeFog = 0.0f;
                        float weightBiomeFog = 0.0f;

                        for (int x = -distance; x <= distance; x++) {
                            for (int z = -distance; z <= distance; z++) {

                                EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
                                int playerX = MathHelper.floor_double(player.posX);
                                int playerY = MathHelper.floor_double(player.posY);
                                int playerZ = MathHelper.floor_double(player.posZ);
                                BiomeGenBase biome = Minecraft.getMinecraft().theWorld
                                        .getBiomeGenForCoords(playerX + x, playerZ + z);

                                if (biome instanceof IBiomeFog) {
                                    float distancePart = ((IBiomeFog) biome)
                                            .getFogDensity(playerX + x, playerY, playerZ + z);
                                    float weightPart = 1.0f;

                                    if (x == -distance) {
                                        double xDiff = 1.0 - (player.posX - playerX);
                                        distancePart *= xDiff;
                                        weightPart *= xDiff;
                                    } else if (x == distance) {
                                        double xDiff = player.posX - playerX;
                                        distancePart *= xDiff;
                                        weightPart *= xDiff;
                                    }

                                    if (z == -distance) {
                                        double zDiff = 1.0 - (player.posZ - playerZ);
                                        distancePart *= zDiff;
                                        weightPart *= zDiff;
                                    } else if (z == distance) {
                                        double zDiff = player.posZ - playerZ;
                                        distancePart *= zDiff;
                                        weightPart *= zDiff;
                                    }

                                    fpDistanceBiomeFog += distancePart;
                                    weightBiomeFog += weightPart;
                                }
                            }
                        }

                        float weightMixed = distance * distance * 4.0f;
                        float weightDefault = weightMixed - weightBiomeFog;
                        float fpDistanceBiomeFogAvg = weightBiomeFog == 0.0f ? 0.0f
                                : fpDistanceBiomeFog / weightBiomeFog;
                        float farPlaneDistance = (fpDistanceBiomeFog * 240.0f + farPlaneDistanceM * weightDefault)
                                / weightMixed;
                        float farPlaneDistanceScaleBiome = (0.1f * (1.0f - fpDistanceBiomeFogAvg)
                                + 0.75f * fpDistanceBiomeFogAvg);

                        AccessorFogHandler.setFogX(Minecraft.getMinecraft().thePlayer.posX);
                        AccessorFogHandler.setFogZ(Minecraft.getMinecraft().thePlayer.posZ);
                        farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75f * weightDefault)
                                / weightMixed;
                        AccessorFogHandler.setFogFarPlaneDistance(Math.min(farPlaneDistance, farPlaneDistanceM));

                        AccessorFogHandler.setFogInit(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ticks.decrementAndGet();

                } else {
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
