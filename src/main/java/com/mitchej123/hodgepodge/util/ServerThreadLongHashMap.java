package com.mitchej123.hodgepodge.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;

/**
 * LongHashMap for server-side use (ChunkProviderServer). Writes are owner-thread-only. Off-thread reads dispatch to the
 * server thread via {@link ServerThreadUtil#callFromMainThread} and block on the result.
 */
@SuppressWarnings("unused")
public class ServerThreadLongHashMap extends FastUtilLongHashMap {

    private static final Logger LOGGER = LogManager.getLogger("ServerThreadLongHashMap");
    private static final Set<String> loggedThreads = ConcurrentHashMap.newKeySet();

    private void logOffThread() {
        final String name = Thread.currentThread().getName();
        if (loggedThreads.add(name)) {
            LOGGER.warn(
                    "Off-thread read from {} — dispatching to server thread",
                    name,
                    new Throwable("Caller stacktrace"));
        }
    }

    @Override
    public Object getValueByKey(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.get(key);
        }
        logOffThread();
        try {
            return ServerThreadUtil.callFromMainThread(() -> map.get(key)).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public boolean containsItem(long key) {
        if (Thread.currentThread() == ownerThread) {
            return map.containsKey(key);
        }
        logOffThread();
        try {
            return ServerThreadUtil.callFromMainThread(() -> map.containsKey(key)).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public int getNumHashElements() {
        if (Thread.currentThread() == ownerThread) {
            return map.size();
        }
        logOffThread();
        try {
            return ServerThreadUtil.callFromMainThread(map::size).get();
        } catch (InterruptedException | ExecutionException e) {
            return 0;
        }
    }

    @Override
    public Iterator<Object> valuesIterator() {
        if (Thread.currentThread() == ownerThread) {
            return map.values().iterator();
        }
        logOffThread();
        try {
            return ServerThreadUtil.callFromMainThread(() -> new ArrayList<>(map.values()).iterator()).get();
        } catch (InterruptedException | ExecutionException e) {
            return Collections.emptyIterator();
        }
    }
}
