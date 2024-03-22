package com.mitchej123.hodgepodge.mixins.hooks;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.common.ModContainer;

public class IC2ResourcePack implements IResourcePack {

    private final IResourcePack fallbackResourcePack;
    private final ZipFile zipFile;

    public IC2ResourcePack(ModContainer container) {
        this.fallbackResourcePack = new FMLFileResourcePack(container);
        try {
            this.zipFile = new ZipFile(container.getSource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException {
        String locationName = locationToName(rl);
        ZipEntry zipentry = zipFile.getEntry(locationName);

        if (zipentry != null) {
            return zipFile.getInputStream(zipentry);
        }
        return fallbackResourcePack.getInputStream(rl);
    }

    @Override
    public boolean resourceExists(ResourceLocation rl) {
        return zipFile.getEntry(locationToName(rl)) != null || fallbackResourcePack.resourceExists(rl);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set getResourceDomains() {
        return ImmutableSet.of("ic2", "minecraft");
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return fallbackResourcePack.getPackMetadata(p_135058_1_, p_135058_2_);
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return fallbackResourcePack.getPackImage();
    }

    @Override
    public String getPackName() {
        return "IC2 resource pack injected by Hodgepodge";
    }

    private static String locationToName(ResourceLocation rl) {
        return String.format("%s/%s", rl.getResourceDomain(), rl.getResourcePath().replace(".lang", ".properties"));
    }
}
