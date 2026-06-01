package com.mitchej123.hodgepodge.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.io.Charsets;

import com.mitchej123.hodgepodge.Common;

/**
 * Iterator which reads lines of an InputStream is a different thread so that the stream can continue to be read while
 * earlier lines are being processed.
 */
public class OffThreadLineIterator implements Iterator<String> {

    private final BufferedReader reader;
    private final LinkedBlockingQueue<String> pendingLines = new LinkedBlockingQueue<>();
    private boolean finished;
    private String next;

    public OffThreadLineIterator(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream, Charsets.toCharset(Charsets.UTF_8)));
        Thread thread = new Thread(this::run, "OffThreadLineIterator");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public boolean hasNext() {
        try {
            next = pendingLines.take();
        } catch (InterruptedException e) {
            return false;
        }
        return !finished || !pendingLines.isEmpty();
    }

    @Override
    public String next() {
        return next;
    }

    private void run() {
        try {
            String line = reader.readLine();
            while (line != null) {
                pendingLines.add(line);
                line = reader.readLine();
            }
        } catch (Exception ex) {
            Common.log.error("Error reading line", ex);
        }
        finished = true;
        pendingLines.add("");
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
