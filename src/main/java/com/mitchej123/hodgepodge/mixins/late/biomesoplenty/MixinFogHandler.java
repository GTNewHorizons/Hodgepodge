package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import biomesoplenty.client.fog.FogHandler;
import biomesoplenty.client.fog.IBiomeFog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FogHandler.class)
public class MixinFogHandler {

    @Shadow(remap = false)
    private static double fogX, fogZ;

    @Shadow(remap = false)
    private static boolean fogInit;

    @Shadow(remap = false)
    private static float fogFarPlaneDistance;

    private static float farPlaneDistanceScale = 0.75f;
    private static float farPlaneDistanceM;

    /**
     * @author glowredman, TataTawa
     * @reason It is not useful to calculate 1600 times per frame only to get newest {@link #fogX} and {@link #fogZ}
     */
    @Overwrite(remap = false)
    @SubscribeEvent
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event) {
        EntityLivingBase entity = event.entity;
        int playerX = MathHelper.floor_double(entity.posX);
        int playerZ = MathHelper.floor_double(entity.posZ);

        if (playerX == fogX && playerZ == fogZ && fogInit) renderFog(event.fogMode, fogFarPlaneDistance, 0.75f);

        farPlaneDistanceM = event.farPlaneDistance;

        if (ticks.get() < -50) {
            new ClientTickThread().start();
            ticks.set(0);
        }

        if (Math.random() < 0.1) ticks.incrementAndGet();

        renderFog(event.fogMode, fogFarPlaneDistance, farPlaneDistanceScale);
    }

    private static AtomicInteger ticks = new AtomicInteger(-100);

    private class ClientTickThread extends Thread {

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
                                BiomeGenBase biome = Minecraft.getMinecraft()
                                        .theWorld
                                        .getBiomeGenForCoords(playerX + x, playerZ + z);

                                if (biome instanceof IBiomeFog) {
                                    float distancePart =
                                            ((IBiomeFog) biome).getFogDensity(playerX + x, playerY, playerZ + z);
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
                        float fpDistanceBiomeFogAvg =
                                weightBiomeFog == 0.0f ? 0.0f : fpDistanceBiomeFog / weightBiomeFog;
                        float farPlaneDistance =
                                (fpDistanceBiomeFog * 240.0f + farPlaneDistanceM * weightDefault) / weightMixed;
                        float farPlaneDistanceScaleBiome =
                                (0.1f * (1.0f - fpDistanceBiomeFogAvg) + 0.75f * fpDistanceBiomeFogAvg);

                        fogX = Minecraft.getMinecraft().thePlayer.posX;
                        fogZ = Minecraft.getMinecraft().thePlayer.posZ;
                        farPlaneDistanceScale =
                                (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75f * weightDefault) / weightMixed;
                        fogFarPlaneDistance = Math.min(farPlaneDistance, farPlaneDistanceM);

                        fogInit = true;

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

    @Shadow(remap = false)
    private static void renderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale) {}
}
