package hodgepodge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import org.junit.jupiter.api.Test;

import com.mitchej123.hodgepodge.mixins.early.minecraft.MixinAnvilChunkLoader_FastChunkWrite;

class ChunkWriteConcurrencyTest {

    @Test
    void concurrentWritesPreserveNbtAcrossBufferResizes() throws Exception {
        Object writer = new MixinAnvilChunkLoader_FastChunkWrite();
        Method write = writer.getClass()
                .getDeclaredMethod("hodgepodge$batchedNBTWrite", NBTTagCompound.class, DataOutput.class);
        write.setAccessible(true);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            Future<?>[] workers = new Future<?>[2];
            for (int worker = 0; worker < workers.length; worker++) {
                final int id = worker;
                workers[worker] = executor.submit(() -> {
                    NBTTagCompound small = new NBTTagCompound();
                    small.setString("mOwnerUuid", "00000000-0000-0000-0000-00000000000" + id);
                    NBTTagCompound large = (NBTTagCompound) small.copy();
                    large.setByteArray("payload", new byte[65536]);
                    byte[][] expected = new byte[2][];
                    NBTTagCompound[] tags = { small, large };
                    for (int i = 0; i < tags.length; i++) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        CompressedStreamTools.write(tags[i], new DataOutputStream(bytes));
                        expected[i] = bytes.toByteArray();
                    }
                    start.await();
                    for (int i = 0; i < 4096; i++) {
                        int size = i % 512 == 0 ? 1 : 0;
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        write.invoke(writer, tags[size], new DataOutputStream(bytes));
                        assertArrayEquals(expected[size], bytes.toByteArray(), "worker " + id + ", write " + i);
                    }
                    return null;
                });
            }
            start.countDown();
            for (Future<?> worker : workers) {
                worker.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    @Test
    void blockedOutputDoesNotBlockSerializationOrChangeItsSnapshot() throws Exception {
        Object writer = new MixinAnvilChunkLoader_FastChunkWrite();
        Method write = writer.getClass()
                .getDeclaredMethod("hodgepodge$batchedNBTWrite", NBTTagCompound.class, DataOutput.class);
        write.setAccessible(true);
        NBTTagCompound first = new NBTTagCompound();
        first.setString("mOwnerUuid", "first");
        NBTTagCompound second = new NBTTagCompound();
        second.setString("mOwnerUuid", "second");
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        CompressedStreamTools.write(first, new DataOutputStream(expected));
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        CountDownLatch outputStarted = new CountDownLatch(1);
        CountDownLatch releaseOutput = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<?> firstWrite = executor.submit(() -> {
                write.invoke(writer, first, new DataOutputStream(actual) {

                    @Override
                    public void write(byte[] bytes) throws IOException {
                        outputStarted.countDown();
                        try {
                            if (!releaseOutput.await(30, TimeUnit.SECONDS)) {
                                throw new IOException("Timed out waiting to release output");
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new IOException(e);
                        }
                        super.write(bytes);
                    }
                });
                return null;
            });
            assertTrue(outputStarted.await(10, TimeUnit.SECONDS));
            Future<?> secondWrite = executor.submit(() -> {
                write.invoke(writer, second, new DataOutputStream(new ByteArrayOutputStream()));
                return null;
            });
            secondWrite.get(10, TimeUnit.SECONDS);
            releaseOutput.countDown();
            firstWrite.get(10, TimeUnit.SECONDS);
            assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        } finally {
            releaseOutput.countDown();
            executor.shutdownNow();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

}
