package com.mitchej123.hodgepodge.commands;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;

public final class DumpTextureAtlasCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "dumptextureatlas";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            dumpAtlas(TextureMap.locationBlocksTexture);
        } catch (Exception e) {
            e.printStackTrace();
            sender.addChatMessage(
                    new ChatComponentText(
                            EnumChatFormatting.RED
                                    + "Failed to copy the block texture atlas. See logs for more info."));
        }
        try {
            dumpAtlas(TextureMap.locationItemsTexture);
        } catch (Exception e) {
            e.printStackTrace();
            sender.addChatMessage(
                    new ChatComponentText(
                            EnumChatFormatting.RED + "Failed to copy the item texture atlas. See logs for more info."));
        }
        sender.addChatMessage(new ChatComponentText("Copied both atlases to your /.minecraft/debug folder."));
    }

    private static void dumpAtlas(ResourceLocation location) throws Exception {
        final TextureMap map = (TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(location);

        final boolean isObf = HodgepodgeCore.isObf();
        Field f = TextureMap.class.getDeclaredField(isObf ? "field_94254_c" : "basePath");
        f.setAccessible(true);
        String basePath = (String) f.get(map);
        f = TextureMap.class.getDeclaredField(isObf ? "field_110574_e" : "mapRegisteredSprites");
        f.setAccessible(true);
        Map<String, TextureAtlasSprite> mapRegisteredSprites = (Map<String, TextureAtlasSprite>) f.get(map);

        File dir = new File(Minecraft.getMinecraft().mcDataDir, "debug");
        dir = new File(dir, basePath);
        if (!dir.exists()) dir.mkdirs();

        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
        copyBoundTextureToFile(new File(dir, "atlas.png"));

        StringBuilder allEntries = new StringBuilder();
        StringBuilder emptyEntries = new StringBuilder();
        StringBuilder erroredEntries = new StringBuilder();
        List<String> sprites = new ArrayList<>(mapRegisteredSprites.keySet());

        sprites.sort(Comparator.comparing(name -> new ResourceLocation(name).getResourceDomain()));

        for (String sprite : sprites) {
            ResourceLocation resourceLocation = completeResourceLocation(basePath, new ResourceLocation(sprite));
            allEntries.append(resourceLocation).append('\n');
            try {
                InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation)
                        .getInputStream();
                BufferedImage image = ImageIO.read(is);
                if (isImageEmpty(image)) {
                    emptyEntries.append(resourceLocation).append('\n');
                }
                is.close();

            } catch (Exception e) {
                erroredEntries.append(resourceLocation).append('\n');
            }
        }

        Files.write(new File(dir, "allEntries.txt").toPath(), allEntries.toString().getBytes(StandardCharsets.UTF_8));
        Files.write(
                new File(dir, "emptyEntries.txt").toPath(),
                emptyEntries.toString().getBytes(StandardCharsets.UTF_8));
        Files.write(
                new File(dir, "erroredEntries.txt").toPath(),
                erroredEntries.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static boolean isImageEmpty(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.getRGB(x, y);

                int alpha = (argb >> 24) & 0xFF;

                if (alpha != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private static ResourceLocation completeResourceLocation(String basePath, ResourceLocation location) {
        return new ResourceLocation(
                location.getResourceDomain(),
                String.format("%s/%s%s", basePath, location.getResourcePath(), ".png"));
    }

    private static void copyBoundTextureToFile(File file) {
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final int[] pixelValues = ((DataBufferInt) bufferedimage.getRaster().getDataBuffer()).getData();
        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(pixelValues.length);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        pixelBuffer.get(pixelValues);

        try {
            ImageIO.write(bufferedimage, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
