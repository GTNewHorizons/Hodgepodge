package com.mitchej123.hodgepodge.core.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.routing.RoutingAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.LoggerContextFactory;

/**
 * An Asynchronous File Logger for FML <br>
 * NOTE: Not using AsyncAppender from log4j beta9, because it blocks or drops on a full queue, deadlocks on reentry, and
 * stop() discards its queue.
 */
public final class AsyncFmlFileLog {

    private static final String LOGGER_NAME = "HodgepodgeAsyncLog";
    private static final Logger LOGGER = LogManager.getLogger(LOGGER_NAME);

    static final String NAME = "FmlFile";
    static final String THREAD = "Hodgepodge-FmlFileAsync";
    static final int SOFT_CAP = 1 << 16;
    static final long STALL_NS = 100_000_000L;
    static final long PARK_NS = 50_000L;
    static final long SPIN_NS = 20_000L;
    static final long DURABLE_NS = 30_000_000_000L;

    private static final int RUNNING = 0;
    private static final int STOPPING = 1;
    private static final int STOPPED = 2;

    private AsyncFmlFileLog() {}

    public static void install() {
        try {
            if (!linkedToLaunchLog4j()) return;
            final List<String> wrapped = install(resolve(LogManager.getFactory()), SOFT_CAP, DURABLE_NS, STALL_NS);
            LOGGER.info("Async FmlFile log: wrapped {}", wrapped);
        } catch (Throwable t) {
            LOGGER.warn("Async FmlFile log install failed", t);
        }
    }

    static List<String> install(List<LoggerContext> contexts, int softCap, long durableNs, long stallNs) {
        final List<LoggerContext> targets = new ArrayList<>();
        Core core = null;
        for (LoggerContext ctx : contexts) {
            final Object cur = ctx.getConfiguration().getAppenders().get(NAME);
            if (cur instanceof RoutingAppender) {
                targets.add(ctx);
            } else if (core == null && cur instanceof Appender ours && !ours.core.isClosed()) {
                core = ours.core;
            }
        }
        final List<String> wrapped = new ArrayList<>();
        if (targets.isEmpty()) return wrapped;
        final boolean created = core == null;
        if (created) core = new Core(softCap, durableNs, stallNs);
        for (LoggerContext ctx : targets) {
            try {
                wrap(ctx, core);
                wrapped.add(ctx.getName());
            } catch (Throwable t) {
                LOGGER.warn("Failed to wrap FmlFile appender of context {}", ctx.getName(), t);
            }
        }
        if (created) core.close();
        return wrapped;
    }

    public static void stopAll() {
        forEachOurs(null, "stop", Appender::stop);
    }

    public static void enterDurableMode() {
        enterDurableMode(null);
    }

    static void enterDurableMode(List<LoggerContext> contexts) {
        final boolean[] latched = new boolean[1];
        forEachOurs(contexts, "enterDurableMode", a -> latched[0] |= a.core.enterDurable());
        try {
            if (latched[0]) LOGGER.info("Async FmlFile log: crash report saved, logging is durable for a while");
        } catch (Throwable ignored) {}
    }

    // NOTE: Must not throw - exit and crash paths.
    private static void forEachOurs(List<LoggerContext> contexts, String what, Consumer<Appender> action) {
        try {
            if (contexts == null) {
                if (!linkedToLaunchLog4j()) return;
                contexts = resolve(LogManager.getFactory());
            }
            for (LoggerContext ctx : contexts) {
                try {
                    final Appender ours = ours(ctx);
                    if (ours != null) action.accept(ours);
                } catch (Throwable t) {
                    LOGGER.warn("Async FmlFile log {} failed for context {}", what, ctx.getName(), t);
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Async FmlFile log {} failed", what, t);
        }
    }

    static long droppedCount(LoggerContext ctx) {
        final Appender ours = ours(ctx);
        return ours == null ? -1 : ours.core.dropped.get();
    }

    private static boolean linkedToLaunchLog4j() {
        final Class<?> launch;
        try {
            launch = Class.forName("net.minecraft.launchwrapper.Launch", false, AsyncFmlFileLog.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            return true;
        }
        Class<?> launchLogManager = null;
        try {
            launchLogManager = Class.forName("org.apache.logging.log4j.LogManager", false, launch.getClassLoader());
        } catch (ClassNotFoundException ignored) {}
        if (launchLogManager == LogManager.class) return true;
        LOGGER.warn(
                "Async FmlFile log skipped: log4j linked from {} but launchwrapper uses {}",
                loaderName(LogManager.class.getClassLoader()),
                launchLogManager == null ? "none" : loaderName(launchLogManager.getClassLoader()));
        return false;
    }

    static List<LoggerContext> resolve(LoggerContextFactory factory) {
        final List<LoggerContext> out = new ArrayList<>(new ClassLoaderContextSelector().getLoggerContexts());
        if (out.isEmpty()) {
            final Object ctx = factory.getContext(LogManager.class.getName(), null, false);
            if (ctx instanceof LoggerContext) out.add((LoggerContext) ctx);
        }
        return out;
    }

    private static Appender ours(LoggerContext ctx) {
        final Object a = ctx.getConfiguration().getAppenders().get(NAME);
        return a instanceof Appender ? (Appender) a : null;
    }

    private static void wrap(LoggerContext ctx, Core core) {
        final Configuration config = ctx.getConfiguration();
        final RoutingAppender cur = (RoutingAppender) config.getAppenders().get(NAME);
        final Appender a = new Appender(cur, core);
        a.start();
        try {
            config.getAppenders().put(NAME, a);
            rewire(config.getLoggerConfig(""), cur, a);
            for (LoggerConfig lc : config.getLoggers().values()) {
                rewire(lc, cur, a);
            }
            if (!core.attach(a)) throw new IllegalStateException("Async FmlFile log worker closed during install");
        } catch (Throwable t) {
            a.stop();
            throw t;
        }
    }

    private static void rewire(LoggerConfig lc, Object cur, Appender a) {
        if (lc.getAppenders().get(NAME) != cur) return;
        AppenderRef named = null;
        final List<AppenderRef> refs = lc.getAppenderRefs();
        if (refs != null) {
            for (AppenderRef ref : refs) {
                if (NAME.equals(ref.getRef())) {
                    named = ref;
                    break;
                }
            }
        }
        lc.addAppender(a, named == null ? null : named.getLevel(), named == null ? null : named.getFilter());
    }

    // Caller thread only
    private static LogEvent pin(LogEvent e) {
        final String thread = e.getThreadName();
        final Message m = e.getMessage();
        final Class<?> mc = m == null ? null : m.getClass();
        final boolean safe = mc == null || mc == SimpleMessage.class || mc == ParameterizedMessage.class;
        if (safe && e instanceof Log4jLogEvent) return e;
        return new Log4jLogEvent(
                e.getLoggerName(),
                e.getMarker(),
                e.getFQCN(),
                e.getLevel(),
                safe ? m : new SimpleMessage(m.getFormattedMessage()),
                e.getThrown(),
                e.getContextMap(),
                e.getContextStack(),
                thread,
                null,
                e.getMillis());
    }

    static boolean pause() {
        final boolean interrupted = Thread.interrupted();
        LockSupport.parkNanos(PARK_NS);
        return interrupted;
    }

    private static String loaderName(ClassLoader loader) {
        return loader == null ? "bootstrap" : loader.getClass().getName();
    }

    private static final class Item {

        final LogEvent event;
        final Appender owner;
        volatile boolean done;

        Item(LogEvent event, Appender owner) {
            this.event = event;
            this.owner = owner;
        }
    }

    private static final class Core implements Runnable {

        final ConcurrentLinkedQueue<Item> queue = new ConcurrentLinkedQueue<>();
        final List<Appender> appenders = new ArrayList<>();
        final AtomicLong reserved = new AtomicLong();
        final AtomicLong dropped = new AtomicLong();
        final Item sentinel = new Item(null, null);
        final int softCap;
        final long hardCap;
        final long durableNs;
        final long stallNs;
        final Thread worker;
        volatile boolean parked;
        volatile long written;
        volatile long stalledAt = -1;
        volatile long durableUntil;
        volatile long stopStalledAt = -1;
        private long droppedReported;
        private long lostThrough;
        private boolean closed;

        Core(int softCap, long durableNs, long stallNs) {
            this.softCap = softCap;
            this.hardCap = 2L * softCap;
            this.durableNs = durableNs;
            this.stallNs = stallNs;
            worker = new Thread(this, THREAD);
            worker.setDaemon(true);
            worker.start();
        }

        @Override
        public void run() {
            boolean done = false;
            while (true) {
                final Item item = queue.poll();
                if (item == null) {
                    if (done) return;
                    idle();
                } else if (item == sentinel) {
                    done = true;
                } else {
                    process(item);
                }
            }
        }

        private void idle() {
            final long start = System.nanoTime();
            while (queue.isEmpty()) {
                if (System.nanoTime() - start < SPIN_NS) {
                    Thread.yield();
                    continue;
                }
                parked = true;
                if (queue.isEmpty()) {
                    Thread.interrupted();
                    LockSupport.park(this);
                }
                parked = false;
                return;
            }
        }

        private void process(Item item) {
            final Appender owner = item.owner;
            try {
                if (!owner.sinkStalled) {
                    owner.target.callAppender(item.event);
                    reportDropped(item);
                }
            } catch (Throwable t) {
                try {
                    owner.routing.getHandler().error("Async FmlFile log worker failed", t);
                } catch (Throwable ignored) {}
            } finally {
                item.done = true;
                written++;
            }
        }

        private void reportDropped(Item item) {
            final long d = dropped.get();
            if (d <= droppedReported) return;
            final long n = d - droppedReported;
            droppedReported = d;
            item.owner.target.callAppender(
                    new Log4jLogEvent(
                            LOGGER_NAME,
                            null,
                            AsyncFmlFileLog.class.getName(),
                            Level.WARN,
                            new SimpleMessage(
                                    "Hodgepodge: async FmlFile log dropped " + n
                                            + " lines while the log sink was stalled"),
                            null,
                            item.event.getContextMap(),
                            null,
                            worker.getName(),
                            null,
                            System.currentTimeMillis()));
        }

        synchronized boolean isClosed() {
            return closed;
        }

        synchronized boolean attach(Appender a) {
            if (closed) return false;
            appenders.add(a);
            return true;
        }

        synchronized void detach(Appender a) {
            if (appenders.remove(a) && appenders.isEmpty()) close();
        }

        synchronized void close() {
            if (closed || !appenders.isEmpty()) return;
            closed = true;
            queue.offer(sentinel);
            if (parked) LockSupport.unpark(worker);
        }

        synchronized long lose(long ticket) {
            final long n = ticket - Math.max(written, lostThrough);
            if (n <= 0) return 0;
            lostThrough = ticket;
            dropped.addAndGet(n);
            return n;
        }

        boolean durable() {
            final long until = durableUntil;
            return until != 0 && System.nanoTime() - until < 0;
        }

        boolean overHardCap() {
            final long w = written;
            return w == stalledAt && reserved.get() - w > hardCap;
        }

        void throttle() {
            final long ticket = reserved.get() - softCap;
            final long w = written;
            if (w < ticket && w != stalledAt) awaitWritten(() -> written >= ticket);
        }

        // Never calls the sink directly: the worker may be stuck on a loader monitor while holding the manager's.
        void awaitWritten(BooleanSupplier done) {
            if (written != stalledAt && !awaitProgress(done, true)) stalledAt = written;
        }

        boolean enterDurable() {
            final boolean wasOff = !durable();
            durableUntil = System.nanoTime() + durableNs;
            final long ticket = reserved.get();
            if (Thread.currentThread() != worker) awaitWritten(() -> written >= ticket);
            return wasOff;
        }

        boolean awaitProgress(BooleanSupplier done, boolean resetOnWrite) {
            boolean interrupted = false;
            long last = written;
            long since = System.nanoTime();
            try {
                while (!done.getAsBoolean()) {
                    interrupted |= pause();
                    final long w = written;
                    if (resetOnWrite && w != last) {
                        last = w;
                        since = System.nanoTime();
                    } else if (System.nanoTime() - since > stallNs) {
                        return false;
                    }
                }
                return true;
            } finally {
                if (interrupted) Thread.currentThread().interrupt();
            }
        }
    }

    private static final class Appender extends AbstractAppender {

        final RoutingAppender routing;
        final AppenderControl target;
        final Core core;
        final AtomicInteger inFlight = new AtomicInteger();
        final AtomicInteger state = new AtomicInteger(RUNNING);
        volatile boolean accepting = true;
        volatile boolean sinkStalled;

        Appender(RoutingAppender routing, Core core) {
            super(NAME, null, null, true);
            this.routing = routing;
            this.target = new AppenderControl(routing, null, null);
            this.core = core;
        }

        @Override
        public void append(LogEvent event) {
            final Core core = this.core;
            if (Thread.currentThread() == core.worker) {
                target.callAppender(event);
                return;
            }
            if (core.overHardCap()) {
                core.dropped.incrementAndGet();
                return;
            }
            final LogEvent pinned = pin(event);
            inFlight.incrementAndGet();
            if (accepting) {
                core.reserved.incrementAndGet();
                final Item item = new Item(pinned, this);
                core.queue.offer(item);
                if (core.parked) LockSupport.unpark(core.worker);
                inFlight.decrementAndGet();
                if (core.durable()) {
                    core.awaitWritten(() -> item.done);
                } else {
                    core.throttle();
                }
                return;
            }
            inFlight.decrementAndGet();
            awaitStopped();
            if (sinkStalled) {
                core.dropped.incrementAndGet();
            } else {
                target.callAppender(event);
            }
        }

        // Unbounded: stop() is bounded, and returning early would reorder or skip the drain.
        private void awaitStopped() {
            boolean interrupted = false;
            try {
                while (state.get() != STOPPED) interrupted |= pause();
            } finally {
                if (interrupted) Thread.currentThread().interrupt();
            }
        }

        @Override
        public void stop() {
            if (!state.compareAndSet(RUNNING, STOPPING)) {
                awaitStopped();
                return;
            }
            long lost = 0;
            try {
                accepting = false;
                core.awaitProgress(() -> inFlight.get() == 0, false);
                final long ticket = core.reserved.get();
                if (core.written < ticket && (core.written == core.stopStalledAt
                        || !core.awaitProgress(() -> core.written >= ticket, true))) {
                    core.stalledAt = core.stopStalledAt = core.written;
                    sinkStalled = true;
                    lost = core.lose(ticket);
                }
                routing.stop();
                super.stop();
            } finally {
                try {
                    core.detach(this);
                } finally {
                    state.set(STOPPED);
                }
            }
            if (sinkStalled) {
                System.err.println("Hodgepodge: async FmlFile log sink stalled, dropped " + lost + " queued lines");
            }
        }
    }
}
