package com.mitchej123.hodgepodge.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

import com.mitchej123.hodgepodge.Common;

public class VoxelMapCacheMover {

    public static void changeFileExtensions(File mcDir) {
        final Path cache = mcDir.toPath().resolve("mods").resolve("VoxelMods").resolve("voxelMap").resolve("cache");
        if (Files.notExists(cache)) {
            return;
        }
        final Visitor visitor = new Visitor();
        try {
            Files.walkFileTree(cache, visitor);
        } catch (IOException e) {
            Common.log.error("Failed to walk cache file tree", e);
            return;
        }
        Common.log.info(
                "Successfully changed the extension of {} cache files ({} failed, {} ignored)",
                visitor.renamed,
                visitor.failed,
                visitor.ignored);
    }

    private static class Visitor extends SimpleFileVisitor<Path> {

        private static final Pattern PATTERN = Pattern.compile("-?\\d+,-?\\d+\\.zip");

        private int renamed = 0;
        private int failed = 0;
        private int ignored = 0;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            super.visitFile(file, attrs);

            String name = file.getFileName().toString();
            if (PATTERN.matcher(name).matches()) {
                try {
                    Path newFile = file.resolveSibling(name.replace(".zip", ".data"));
                    if (Files.exists(newFile) && (!Files.isSameFile(newFile, file)
                            || Files.getLastModifiedTime(newFile).compareTo(Files.getLastModifiedTime(file)) > 0)) {
                        this.ignored++;
                        return FileVisitResult.CONTINUE;
                    }
                    Files.move(file, file.resolveSibling(name.replace(".zip", ".data")));
                    this.renamed++;
                } catch (IOException e) {
                    Common.log.warn("Failed to change extension of " + file + " to .data", e);
                    this.failed++;
                }
            } else {
                this.ignored++;
            }

            return FileVisitResult.CONTINUE;
        }

    }

}
