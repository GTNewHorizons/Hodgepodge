package com.mitchej123.hodgepodge.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import com.mitchej123.hodgepodge.core.HodgepodgeCore;

public class FileLogger implements AutoCloseable {

    private final SimpleDateFormat dateFormat;
    private PrintStream printStream = null;

    public FileLogger(String filename) {
        this(HodgepodgeCore.getMcLocation(), filename);
    }

    public FileLogger(File logFolder, String filename) {
        this(logFolder, filename, null);
    }

    // timeFormat "HH:mm:ss"
    public FileLogger(File logFolder, String filename, String timeFormat) {
        if (timeFormat == null) {
            dateFormat = null;
        } else {
            dateFormat = new SimpleDateFormat(timeFormat);
        }
        if (logFolder == null) {
            throw new IllegalStateException("FileLogger logFolder cannot be null!");
        }
        if (filename == null) {
            throw new IllegalStateException("FileLogger file name cannot be null!");
        }
        // noinspection ResultOfMethodCallIgnored
        logFolder.mkdirs();
        final File logFile = new File(logFolder, filename);
        if (logFile.exists()) {
            // noinspection ResultOfMethodCallIgnored
            logFile.delete();
        }
        if (!logFile.exists()) {
            try {
                // noinspection ResultOfMethodCallIgnored
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            printStream = new PrintStream(new FileOutputStream(logFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        if (printStream == null) return;
        if (dateFormat == null) {
            printStream.println(message);
            return;
        }
        final String time = dateFormat.format(System.currentTimeMillis());
        printStream.println("[" + time + "] " + message);
    }

    public void printStackTrace() {
        if (printStream == null) return;
        new Exception().printStackTrace(printStream);
    }

    public void close() {
        if (printStream == null) return;
        printStream.close();
    }
}
