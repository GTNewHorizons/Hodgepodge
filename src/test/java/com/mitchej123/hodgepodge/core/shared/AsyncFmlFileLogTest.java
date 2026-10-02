package com.mitchej123.hodgepodge.core.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.routing.RoutingAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.XMLConfiguration;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

@Timeout(120)
class AsyncFmlFileLogTest {

    private static final String XML = "/hodgepodge-test-log4j2.xml";
    private static final String DIR_PROP = "hodgepodge.test.logdir";
    private static final String SIDE_FILE = "logs/fml-CLIENT-latest.log";
    private static final String JUNK_FILE = "logs/fml-junk-earlystartup.log";
    private static final Pattern SEQ = Pattern.compile("]: seq (\\d+)$");
    private static final Pattern HEADER = Pattern.compile("^\\[\\d\\d:\\d\\d:\\d\\d\\] \\[([^/]*)/");
    private static final long STALL_MS = 500;
    private static final long STALL_NS = TimeUnit.MILLISECONDS.toNanos(STALL_MS);

    @TempDir
    Path tmp;

    private final List<LoggerContext> contexts = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();

    @AfterEach
    void tearDown() {
        for (Gate g : gates) g.open.countDown();
        for (LoggerContext ctx : contexts) ctx.stop();
        ThreadContext.clear();
        System.clearProperty(DIR_PROP);
    }

    @Test
    void asyncOutputMatchesSync() throws Exception {
        Path syncDir = tmp.resolve("sync");
        LoggerContext sync = newContext(syncDir);
        runWorkload(sync);
        sync.stop();

        Path asyncDir = tmp.resolve("async");
        LoggerContext async = newContext(asyncDir);
        assertTrue(install(async));
        runWorkload(async);
        async.stop();

        for (String file : Arrays.asList(SIDE_FILE, JUNK_FILE)) {
            List<String> expected = records(syncDir.resolve(file));
            List<String> actual = records(asyncDir.resolve(file));
            assertFalse(expected.isEmpty(), file);
            Map<String, List<String>> expectedByThread = byThread(expected);
            Map<String, List<String>> actualByThread = byThread(actual);
            assertEquals(expectedByThread.keySet(), actualByThread.keySet(), file);
            for (Map.Entry<String, List<String>> e : expectedByThread.entrySet()) {
                assertEquals(e.getValue(), actualByThread.get(e.getKey()), file + " thread " + e.getKey());
            }
        }
    }

    @Test
    void twoContextsShareOneFifo() throws Exception {
        Path dir = tmp.resolve("shared");
        LoggerContext a = newContext("a", dir);
        LoggerContext b = newContext("b", dir);
        assertTrue(install(Arrays.asList(a, b)));
        Logger la = a.getLogger("ctx.a");
        Logger lb = b.getLogger("ctx.b");
        int n = 5000;
        Thread t = new Thread(() -> {
            ThreadContext.put("side", "CLIENT");
            for (int i = 0; i < n; i++) {
                (i % 3 == 0 ? lb : la).debug("seq {}", i);
            }
        }, "alternating");
        t.start();
        t.join();
        a.stop();
        b.stop();
        assertEquals(range(n), seqs(dir.resolve(SIDE_FILE), "alternating"));
    }

    @Test
    void workerExitsOnceEveryContextStops() throws Exception {
        Path dir = tmp.resolve("exit");
        LoggerContext a = newContext("exit-a", dir);
        LoggerContext b = newContext("exit-b", dir);
        List<Thread> before = workers();
        assertTrue(install(Arrays.asList(a, b)));
        List<Thread> started = workers();
        started.removeAll(before);
        assertEquals(1, started.size(), started.toString());
        Thread worker = started.get(0);
        a.getLogger("exit").debug("seq 0");
        a.stop();
        worker.join(STALL_MS);
        assertTrue(worker.isAlive(), "worker exited while context b still wrapped");
        b.stop();
        worker.join(5_000);
        assertFalse(worker.isAlive(), "worker still running after every context stopped");
    }

    @Test
    void stallIsPaidOncePerCoreNotPerContext() throws Exception {
        Stalled a = stalled("stall-a");
        LoggerContext b = newContext(tmp.resolve("stall-b"));
        Logger lb = b.getLogger("stall.b");
        lb.debug("warmup");
        assertTrue(install(Arrays.asList(a.ctx, b)));
        for (int i = 0; i < 100; i++) {
            a.log.debug("seq {}", i);
            lb.debug("seq {}", i);
        }
        assertTrue(a.gate.entered.await(10, TimeUnit.SECONDS));
        long stopMs = assertTimeoutPreemptively(Duration.ofSeconds(10), () -> elapsedMs(() -> {
            a.ctx.stop();
            b.stop();
        }));
        assertTrue(stopMs < 2 * STALL_MS, "stopping both contexts took " + stopMs + "ms");
    }

    @Test
    void throttledProducerOnInterruptedThreadDoesNotSpin() throws Exception {
        Path dir = tmp.resolve("throttle-intr");
        LoggerContext ctx = newContext(dir);
        Gate gate = slowSink(ctx);
        gate.delayNs = TimeUnit.MILLISECONDS.toNanos(20);
        // softCap 1: every append after the first waits for the previous write.
        assertTrue(install(Collections.singletonList(ctx), 1));
        Logger log = ctx.getLogger("throttle-intr");
        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        int n = 10;
        long[] cpuWall = new long[2];
        AtomicBoolean stillInterrupted = new AtomicBoolean();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();
        Thread t = new Thread(() -> {
            try {
                Thread.currentThread().interrupt();
                long cpu0 = mx.isCurrentThreadCpuTimeSupported() ? mx.getCurrentThreadCpuTime() : -1;
                long wall0 = System.nanoTime();
                logSeqs(log, 0, n);
                cpuWall[1] = System.nanoTime() - wall0;
                cpuWall[0] = cpu0 < 0 ? -1 : mx.getCurrentThreadCpuTime() - cpu0;
                stillInterrupted.set(Thread.currentThread().isInterrupted());
            } catch (Throwable e) {
                errors.add(e);
            }
        }, "interrupted");
        t.start();
        t.join(30_000);
        assertFalse(t.isAlive());
        assertTrue(errors.isEmpty(), () -> errors.peek().toString());
        assertTrue(stillInterrupted.get(), "interrupt flag lost");
        assertTrue(cpuWall[1] >= TimeUnit.MILLISECONDS.toNanos(100), "producer never waited: " + cpuWall[1] + "ns");
        if (cpuWall[0] >= 0) {
            assertTrue(
                    cpuWall[0] < cpuWall[1] / 2,
                    "cpu " + cpuWall[0] / 1_000_000 + "ms of wall " + cpuWall[1] / 1_000_000 + "ms");
        }
        ctx.stop();
        assertEquals(range(n), seqs(dir.resolve(JUNK_FILE), "interrupted"));
    }

    @Test
    void proxiedFactoryStillFindsSelectorContexts() throws Exception {
        LoggerContextFactory proxy = (LoggerContextFactory) Proxy.newProxyInstance(
                AsyncFmlFileLogTest.class.getClassLoader(),
                new Class<?>[] { LoggerContextFactory.class },
                (p, method, args) -> { throw new UnsupportedOperationException(method.getName()); });
        ClassLoaderContextSelector selector = new ClassLoaderContextSelector();
        try (URLClassLoader loader = new URLClassLoader(new URL[0], null)) {
            LoggerContext ctx = selector.getContext(AsyncFmlFileLogTest.class.getName(), loader, false);
            contexts.add(ctx);
            try {
                Path dir = tmp.resolve("proxied");
                System.setProperty(DIR_PROP, dir.toString());
                try (InputStream in = AsyncFmlFileLogTest.class.getResourceAsStream(XML)) {
                    ctx.start(new XMLConfiguration(new ConfigurationFactory.ConfigurationSource(in)));
                }
                List<LoggerContext> found = AsyncFmlFileLog.resolve(proxy);
                assertTrue(found.contains(ctx), found.toString());
                // Only ours: the selector map also holds the test JVM's global context.
                List<LoggerContext> mine = new ArrayList<>();
                for (LoggerContext c : found) if (c == ctx) mine.add(c);
                assertTrue(install(mine));
                assertTrue(isOurs(ctx.getConfiguration().getAppenders().get("FmlFile")));
                ctx.getLogger("proxied").debug("seq {}", 0);
                ctx.stop();
                assertEquals(range(1), mine(dir));
            } finally {
                ctx.stop();
                selector.removeContext(ctx);
            }
        }
    }

    @Test
    void stalledSinkDropsPastHardCapAndReportsOnRecovery() throws Exception {
        Path dir = tmp.resolve("hardcap");
        LoggerContext ctx = newContext(dir);
        Gate gate = gate(ctx);
        int softCap = 8;
        int producers = 4;
        int n = 250;
        assertTrue(install(Collections.singletonList(ctx), softCap));
        List<Thread> threads = new ArrayList<>();
        for (int p = 0; p < producers; p++) {
            Logger log = ctx.getLogger("hardcap." + p);
            threads.add(new Thread(() -> logSeqs(log, 0, n), "hardcap-" + p));
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) {
            t.join(30_000);
            assertFalse(t.isAlive(), t.getName());
        }
        long dropped = AsyncFmlFileLog.droppedCount(ctx);
        assertTrue(dropped > 0, "nothing dropped");
        gate.open.countDown();
        ctx.stop();

        Path file = dir.resolve(JUNK_FILE);
        int kept = 0;
        for (int p = 0; p < producers; p++) {
            List<Integer> seqs = seqs(file, "hardcap-" + p);
            assertEquals(range(seqs.size()), seqs, "hardcap-" + p);
            kept += seqs.size();
        }
        // Racing producers can each pass the cap check once.
        assertTrue(kept <= 2 * softCap + producers, "kept " + kept);
        assertEquals(producers * n, kept + dropped);
        List<String> reports = new ArrayList<>();
        for (String r : records(file)) if (r.contains("async FmlFile log dropped")) reports.add(r);
        assertEquals(1, reports.size(), reports.toString());
        assertTrue(
                reports.get(0).endsWith(
                        "Hodgepodge: async FmlFile log dropped " + dropped + " lines while the log sink was stalled"),
                reports.get(0));
    }

    @Test
    void installIsIdempotent() {
        LoggerContext ctx = newContext(tmp.resolve("idem"));
        assertTrue(install(ctx));
        assertFalse(install(ctx));
        Configuration config = ctx.getConfiguration();
        assertTrue(isOurs(config.getAppenders().get("FmlFile")));
        int ours = 0;
        for (Appender a : config.getLoggerConfig("").getAppenders().values()) {
            if (isOurs(a)) ours++;
            assertFalse(a instanceof RoutingAppender);
        }
        assertEquals(1, ours);
    }

    @Test
    void mutableMessageIsFormattedAtCallTime() throws Exception {
        Path dir = tmp.resolve("mutable");
        LoggerContext ctx = newContext(dir);
        Gate gate = gate(ctx);
        assertTrue(install(ctx));
        Logger log = ctx.getLogger("mutable");
        StringBuilder value = new StringBuilder("seq 0");
        log.info(new ObjectMessage(value));
        assertTrue(gate.entered.await(10, TimeUnit.SECONDS));
        value.setLength(0);
        value.append("seq 1");
        gate.open.countDown();
        ctx.stop();
        assertEquals(range(1), mine(dir));
    }

    @Test
    void stopRacingProducersLosesNothingReturned() throws Exception {
        Path dir = tmp.resolve("race");
        LoggerContext ctx = newContext(dir);
        assertTrue(install(ctx));
        int producers = 4;
        AtomicInteger[] completed = new AtomicInteger[producers];
        AtomicBoolean stopped = new AtomicBoolean();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();
        List<Thread> threads = new ArrayList<>();
        for (int p = 0; p < producers; p++) {
            AtomicInteger done = completed[p] = new AtomicInteger();
            Logger log = ctx.getLogger("race." + p);
            Thread t = new Thread(() -> {
                try {
                    for (int i = 0; !stopped.get(); i++) {
                        log.debug("seq {}", i);
                        done.set(i + 1);
                    }
                } catch (Throwable e) {
                    errors.add(e);
                }
            }, "producer-" + p);
            threads.add(t);
            t.start();
        }
        for (AtomicInteger c : completed) {
            while (c.get() < 1000) Thread.yield();
        }
        int[] returned = new int[producers];
        for (int p = 0; p < producers; p++) returned[p] = completed[p].get();
        ctx.stop();
        stopped.set(true);
        for (Thread t : threads) t.join(10_000);
        assertTrue(errors.isEmpty(), () -> errors.toString());
        for (int p = 0; p < producers; p++) {
            List<Integer> seqs = seqs(dir.resolve(JUNK_FILE), "producer-" + p);
            assertTrue(
                    seqs.size() >= returned[p],
                    "producer-" + p + " lost lines: " + seqs.size() + " < " + returned[p]);
            assertEquals(range(seqs.size()), seqs, "producer-" + p);
        }
    }

    @Test
    void stalledSinkBoundsProducersAndStop() throws Exception {
        Stalled s = stalled("stall");
        assertTrue(install(Collections.singletonList(s.ctx), 8));
        long maxCallMs = 0;
        for (int i = 0; i < 64; i++) {
            int seq = i;
            maxCallMs = Math.max(maxCallMs, elapsedMs(() -> s.log.debug("seq {}", seq)));
        }
        assertTrue(maxCallMs >= STALL_MS / 2, "no backpressure: max call " + maxCallMs + "ms");
        assertTrue(maxCallMs < 3 * STALL_MS, "over-cap producer blocked " + maxCallMs + "ms");
        long laterMs = elapsedMs(() -> logSeqs(s.log, 64, 1064));
        assertTrue(laterMs < STALL_MS / 4, "later producers waited again: " + laterMs + "ms");

        Appender ours = s.ctx.getConfiguration().getAppenders().get("FmlFile");
        assertTrue(isOurs(ours));
        long stopMs = assertTimeoutPreemptively(Duration.ofSeconds(10), () -> elapsedMs(ours::stop));
        assertTrue(stopMs < 3 * STALL_MS, "stop took " + stopMs + "ms");
        long dropped = AsyncFmlFileLog.droppedCount(s.ctx);
        assertTrue(dropped > 0 && dropped <= 1064, "dropped " + dropped);
    }

    @Test
    void concurrentStopsBothWaitForDrain() throws Exception {
        Path dir = tmp.resolve("stop2");
        LoggerContext ctx = newContext(dir);
        slowSink(ctx);
        assertTrue(install(ctx));
        Logger log = ctx.getLogger("stop2");
        int n = 300;
        logSeqs(log, 0, n);
        Appender ours = ctx.getConfiguration().getAppenders().get("FmlFile");
        assertTrue(isOurs(ours));
        Path file = dir.resolve(JUNK_FILE);
        String main = Thread.currentThread().getName();
        CountDownLatch go = new CountDownLatch(1);
        int[] seen = new int[2];
        Thread[] stoppers = new Thread[2];
        for (int s = 0; s < 2; s++) {
            final int id = s;
            stoppers[s] = new Thread(() -> {
                try {
                    go.await();
                    ours.stop();
                    seen[id] = seqs(file, main).size();
                } catch (Exception e) {
                    seen[id] = -1;
                }
            }, "stopper-" + s);
            stoppers[s].start();
        }
        go.countDown();
        for (Thread t : stoppers) {
            t.join(10_000);
            assertFalse(t.isAlive());
        }
        assertEquals(n, seen[0]);
        assertEquals(n, seen[1]);
        assertEquals(range(n), mine(dir));
    }

    @Test
    void workerReentryNeitherDeadlocksNorRecurses() throws Exception {
        Path dir = tmp.resolve("reentry");
        LoggerContext ctx = newContext(dir);
        Logger inner = ctx.getLogger("inner");
        AtomicInteger calls = new AtomicInteger();
        AtomicInteger returned = new AtomicInteger();
        AbstractFilter reenter = new AbstractFilter() {

            @Override
            public Result filter(LogEvent event) {
                if (event.getMessage().getFormattedMessage().startsWith("seq ")) {
                    calls.incrementAndGet();
                    inner.info("inner");
                    returned.incrementAndGet();
                }
                return Result.NEUTRAL;
            }
        };
        ((RoutingAppender) ctx.getConfiguration().getAppenders().get("FmlFile")).addFilter(reenter);
        assertTrue(install(ctx));
        Logger log = ctx.getLogger("outer");
        int n = 200;
        logSeqs(log, 0, n);
        while (returned.get() < n) Thread.yield();
        ctx.stop();
        assertEquals(n, calls.get());
        List<String> lines = records(dir.resolve(JUNK_FILE));
        assertEquals(range(n), mine(dir));
        for (String line : lines) assertFalse(line.endsWith("]: inner"), line);
    }

    @Test
    void reconfigureDrainsAndFallsBackToSync() throws Exception {
        Path dir = tmp.resolve("reconf");
        LoggerContext ctx = newContext(dir);
        Gate gate = gate(ctx);
        assertTrue(install(ctx));
        Logger log = ctx.getLogger("reconf");
        int n = 500;
        logSeqs(log, 0, n);
        gate.open.countDown();
        ctx.setConfigLocation(AsyncFmlFileLogTest.class.getResource(XML).toURI());
        assertEquals(range(n), mine(dir));
        Appender now = ctx.getConfiguration().getAppenders().get("FmlFile");
        assertInstanceOf(RoutingAppender.class, now);
        logSeqs(log, n, 2 * n);
        assertEquals(range(2 * n), mine(dir));
    }

    private static boolean install(LoggerContext ctx) {
        return install(Collections.singletonList(ctx));
    }

    private static boolean install(List<LoggerContext> list) {
        return install(list, AsyncFmlFileLog.SOFT_CAP);
    }

    private static boolean install(List<LoggerContext> list, int softCap) {
        return !AsyncFmlFileLog.install(list, softCap, STALL_NS).isEmpty();
    }

    private LoggerContext newContext(Path dir) {
        return newContext(dir.getFileName().toString(), dir);
    }

    private LoggerContext newContext(String name, Path dir) {
        System.setProperty(DIR_PROP, dir.toString());
        LoggerContext ctx = new LoggerContext(name);
        contexts.add(ctx);
        try (InputStream in = AsyncFmlFileLogTest.class.getResourceAsStream(XML)) {
            assertNotNull(in, XML);
            ctx.start(new XMLConfiguration(new ConfigurationFactory.ConfigurationSource(in)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ctx;
    }

    private Gate gate(LoggerContext ctx) {
        Gate gate = new Gate();
        gates.add(gate);
        ((RoutingAppender) ctx.getConfiguration().getAppenders().get("FmlFile")).addFilter(gate);
        return gate;
    }

    private static List<Thread> workers() {
        List<Thread> out = new ArrayList<>();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (AsyncFmlFileLog.THREAD.equals(t.getName())) out.add(t);
        }
        return out;
    }

    private Gate slowSink(LoggerContext ctx) {
        Gate gate = gate(ctx);
        gate.delayNs = TimeUnit.MILLISECONDS.toNanos(1);
        gate.open.countDown();
        return gate;
    }

    private Stalled stalled(String name) {
        LoggerContext ctx = newContext(tmp.resolve(name));
        Logger log = ctx.getLogger(name);
        log.debug("warmup");
        return new Stalled(ctx, log, gate(ctx));
    }

    private static final class Stalled {

        final LoggerContext ctx;
        final Logger log;
        final Gate gate;

        Stalled(LoggerContext ctx, Logger log, Gate gate) {
            this.ctx = ctx;
            this.log = log;
            this.gate = gate;
        }
    }

    private static void logSeqs(Logger log, int from, int to) {
        for (int i = from; i < to; i++) log.debug("seq {}", i);
    }

    private static long elapsedMs(Runnable r) {
        long t0 = System.nanoTime();
        r.run();
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);
    }

    private static List<Integer> mine(Path dir) throws IOException {
        return seqs(dir.resolve(JUNK_FILE), Thread.currentThread().getName());
    }

    private static boolean isOurs(Appender a) {
        return a != null && a.getClass().getName().endsWith("AsyncFmlFileLog$Appender");
    }

    private static void runWorkload(LoggerContext ctx) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < 9; t++) {
            threads.add(new Thread(new Workload(ctx, t, t < 8), "w" + t));
        }
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
    }

    private static final class Workload implements Runnable {

        private final Logger a;
        private final Logger b;
        private final int id;
        private final boolean client;

        Workload(LoggerContext ctx, int id, boolean client) {
            this.a = ctx.getLogger("work.a");
            this.b = ctx.getLogger("work.b");
            this.id = id;
            this.client = client;
        }

        @Override
        public void run() {
            if (client) ThreadContext.put("side", "CLIENT");
            for (int i = 0; i < 5000; i++) {
                switch (i % 8) {
                    case 0 -> a.info("simple " + id + " " + i);
                    case 1 -> b.debug("param {} {}", id, i);
                    case 2 -> a.trace("trace " + i);
                    case 3 -> b.warn("warn " + i, boom(i));
                    case 4 -> {
                        ThreadContext.put("mod", "mod" + id);
                        a.debug("with mod {}", i);
                        ThreadContext.remove("mod");
                    }
                    case 5 -> b.error("param thrown {}", i, boom(i));
                    case 6 -> a.debug(new ObjectMessage("object " + i), boom(i));
                    default -> b.trace("three {} {} {}", id, i, null);
                }
            }
        }

        private static Throwable boom(int i) {
            return new IllegalStateException("boom " + i);
        }
    }

    private static final class Gate extends AbstractFilter {

        final CountDownLatch open = new CountDownLatch(1);
        final CountDownLatch entered = new CountDownLatch(1);
        volatile long delayNs;

        @Override
        public Result filter(LogEvent event) {
            entered.countDown();
            boolean interrupted = false;
            while (true) {
                try {
                    open.await();
                    break;
                } catch (InterruptedException e) {
                    interrupted = true;
                }
            }
            if (interrupted) Thread.currentThread().interrupt();
            // Deadline loop: a leftover unpark permit must not shorten the delay.
            final long end = System.nanoTime() + delayNs;
            for (long left = delayNs; left > 0; left = end - System.nanoTime()) LockSupport.parkNanos(left);
            return Result.NEUTRAL;
        }
    }

    private static List<String> records(Path file) throws IOException {
        List<String> out = new ArrayList<>();
        if (!Files.exists(file)) return out;
        StringBuilder cur = null;
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (HEADER.matcher(line).find()) {
                if (cur != null) out.add(cur.toString());
                cur = new StringBuilder(line.substring(11));
            } else {
                assertNotNull(cur, "continuation without header: " + line);
                cur.append('\n').append(line);
            }
        }
        if (cur != null) out.add(cur.toString());
        return out;
    }

    private static Map<String, List<String>> byThread(List<String> records) {
        Map<String, List<String>> out = new LinkedHashMap<>();
        for (String r : records) {
            String thread = r.substring(1, r.indexOf('/'));
            out.computeIfAbsent(thread, k -> new ArrayList<>()).add(r);
        }
        return out;
    }

    private static List<Integer> seqs(Path file, String thread) throws IOException {
        List<Integer> out = new ArrayList<>();
        List<String> own = byThread(records(file)).get(thread);
        if (own == null) return out;
        for (String r : own) {
            Matcher m = SEQ.matcher(r);
            if (m.find()) out.add(Integer.parseInt(m.group(1)));
        }
        return out;
    }

    private static List<Integer> range(int n) {
        List<Integer> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) out.add(i);
        return out;
    }
}
