package com.mitchej123.hodgepodge.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class WorldDataSaverTest {

    @TempDir
    Path temporary;

    @Test
    void replacementPreservesPosixPermissions() throws Exception {
        Path target = Files.createFile(temporary.resolve("permissions.dat"));
        org.junit.jupiter.api.Assumptions.assumeTrue(Files.getFileStore(target).supportsFileAttributeView("posix"));
        Path reference = Files.createFile(temporary.resolve("ordinary-new-file"));
        Path newTarget = temporary.resolve("new-save.dat");
        WorldDataSaver.writeData(newTarget.toFile(), tag("new"), false, false);
        assertEquals(Files.getPosixFilePermissions(reference), Files.getPosixFilePermissions(newTarget));
        java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions = java.nio.file.attribute.PosixFilePermissions
                .fromString("rw-rw----");
        Files.setPosixFilePermissions(target, permissions);
        WorldDataSaver.writeData(target.toFile(), tag("replacement"), false, false);
        assertEquals(permissions, Files.getPosixFilePermissions(target));

        java.util.Set<java.nio.file.attribute.PosixFilePermission> readOnlyOwner = java.nio.file.attribute.PosixFilePermissions
                .fromString("r--rw-rw-");
        Files.setPosixFilePermissions(target, readOnlyOwner);
        WorldDataSaver.writeData(target.toFile(), tag("read-only-owner"), false, false);
        assertEquals(readOnlyOwner, Files.getPosixFilePermissions(target));
        assertEquals("read-only-owner", CompressedStreamTools.read(target.toFile()).getString("value"));
    }

    @Test
    void backupReplacementFailureKeepsTheTargetAndClearsTemporaries() throws Exception {
        Path directory = Files.createDirectory(temporary.resolve("backup-failure"));
        Path target = directory.resolve("data.dat");
        CompressedStreamTools.write(tag("original"), target.toFile());
        Path blocked = Files.createDirectory(directory.resolve("data.dat_old"));
        Files.write(blocked.resolve("keep"), new byte[] { 1 });

        assertThrows(
                IOException.class,
                () -> WorldDataSaver.writeData(target.toFile(), tag("replacement"), false, true));
        assertEquals("original", CompressedStreamTools.read(target.toFile()).getString("value"));
        try (java.util.stream.Stream<Path> leftovers = Files.list(directory)) {
            assertFalse(leftovers.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    void flushWaitsForQueuedWorkToFinish() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        ThreadedFileIOBase.threadedIOInstance.queueIO(new IThreadedFileIO() {

            private boolean done;

            @Override
            public boolean writeNextIO() {
                if (done) return false;
                started.countDown();
                try {
                    release.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                done = true;
                return true;
            }
        });
        assertTrue(started.await(5, TimeUnit.SECONDS));

        AtomicBoolean returned = new AtomicBoolean();
        CountDownLatch flushing = new CountDownLatch(1);
        Thread flusher = new Thread(() -> {
            flushing.countDown();
            try {
                WorldDataSaver.INSTANCE.flush();
                returned.set(true);
            } catch (Exception ignored) {}
        });
        flusher.start();
        try {
            assertTrue(flushing.await(5, TimeUnit.SECONDS));
            flusher.join(200);
            assertTrue(flusher.isAlive(), "flush returned while work was still queued");
        } finally {
            release.countDown();
        }
        flusher.join(TimeUnit.SECONDS.toMillis(5));
        assertTrue(returned.get());
    }

    @Test
    void saveQueuedDuringRemovalGetsANewDrainTask() throws Exception {
        TestSaver saver = new TestSaver();
        File file = temporary.resolve("data.dat").toFile();
        saver.saveData(file, tag("first"), false, false);
        IThreadedFileIO first = saver.tasks.get(0);
        assertTrue(first.writeNextIO());
        assertFalse(first.writeNextIO());

        saver.saveData(file, tag("second"), false, false);
        saver.tasks.remove(first);

        assertEquals(1, saver.tasks.size());
        assertNotSame(first, saver.tasks.get(0));
        assertTrue(saver.tasks.get(0).writeNextIO());
        assertEquals("second", CompressedStreamTools.read(file).getString("value"));
    }

    @Test
    void pendingWritesForTheSameFileKeepTheLatestSnapshot() throws Exception {
        TestSaver saver = new TestSaver();
        File file = temporary.resolve("data.dat").toFile();
        saver.saveData(file, tag("first"), false, false);
        saver.saveData(file, tag("second"), false, false);

        assertEquals(1, saver.tasks.size());
        assertTrue(saver.tasks.get(0).writeNextIO());
        assertEquals("second", CompressedStreamTools.read(file).getString("value"));
    }

    @Test
    void failedSerializationPreservesTheExistingFile() throws Exception {
        File file = temporary.resolve("data.dat").toFile();
        byte[] original = "original".getBytes(StandardCharsets.UTF_8);
        Files.write(file.toPath(), original);

        assertThrows(NullPointerException.class, () -> WorldDataSaver.writeData(file, null, true, false));
        assertArrayEquals(original, Files.readAllBytes(file.toPath()));
    }

    @Test
    void failedWriteIsRetriedOnTheNextSaveEvent() throws Exception {
        TestSaver saver = new TestSaver();
        Path target = Files.createDirectory(temporary.resolve("blocked.dat"));
        Path blocker = Files.write(target.resolve("keep"), new byte[] { 1 });
        saver.saveData(target.toFile(), tag("retried"), false, false);
        IThreadedFileIO first = saver.tasks.get(0);
        assertTrue(first.writeNextIO());
        assertFalse(first.writeNextIO());
        saver.tasks.remove(first);

        Files.delete(blocker);
        Files.delete(target);
        File trigger = temporary.resolve("trigger.dat").toFile();
        saver.saveData(trigger, tag("trigger"), false, false);

        IThreadedFileIO retry = saver.tasks.get(0);
        assertTrue(retry.writeNextIO());
        assertEquals("retried", CompressedStreamTools.read(target.toFile()).getString("value"));
        assertTrue(retry.writeNextIO());
        assertEquals("trigger", CompressedStreamTools.read(trigger).getString("value"));
    }

    @Test
    void backupWriteReplacesTheFileAndKeepsItsPreviousVersion() throws Exception {
        File file = temporary.resolve("level.dat").toFile();
        WorldDataSaver.writeData(file, tag("first"), true, false);
        WorldDataSaver.writeData(file, tag("second"), true, true);

        assertEquals("second", readCompressed(file).getString("value"));
        assertEquals("first", readCompressed(temporary.resolve("level.dat_old").toFile()).getString("value"));
    }

    private static NBTTagCompound readCompressed(File file) throws IOException {
        try (InputStream input = Files.newInputStream(file.toPath())) {
            return CompressedStreamTools.readCompressed(input);
        }
    }

    @Test
    void closedSessionCannotOverwriteARestoredFile() throws Exception {
        TestSaver saver = new TestSaver();
        Path target = Files.createDirectory(temporary.resolve("map.dat"));
        Path blocker = Files.write(target.resolve("keep"), new byte[] { 1 });
        saver.saveData(target.toFile(), tag("old-session"), false, false);
        saver.closeSession();
        Files.delete(blocker);
        Files.delete(target);
        WorldDataSaver.writeData(target.toFile(), tag("restored"), false, false);
        saver.saveData(temporary.resolve("other-world.dat").toFile(), tag("new-session"), false, false);
        saver.flush();
        assertEquals("restored", CompressedStreamTools.read(target.toFile()).getString("value"));
    }

    @Test
    void flushDrainsPendingWritesAndReportsFailures() throws Exception {
        TestSaver saver = new TestSaver();
        File saved = temporary.resolve("saved.dat").toFile();
        saver.saveData(saved, tag("saved"), false, false);
        saver.flush();
        assertEquals("saved", CompressedStreamTools.read(saved).getString("value"));
        Path blocked = Files.createDirectory(temporary.resolve("blocked"));
        Files.write(blocked.resolve("keep"), new byte[] { 1 });
        saver.saveData(blocked.toFile(), tag("failed"), false, false);
        assertThrows(IOException.class, saver::flush);
    }

    private static NBTTagCompound tag(String value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("value", value);
        return tag;
    }

    private static class TestSaver extends WorldDataSaver {

        final List<IThreadedFileIO> tasks = new ArrayList<>();

        @Override
        protected void queueIO(IThreadedFileIO task) {
            if (!tasks.contains(task)) tasks.add(task);
        }

        @Override
        protected void awaitIO() {
            for (IThreadedFileIO task : new ArrayList<>(tasks)) {
                while (task.writeNextIO()) {}
                tasks.remove(task);
            }
        }
    }
}
