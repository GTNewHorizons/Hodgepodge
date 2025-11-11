package com.mitchej123.hodgepodge.core.shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import net.minecraft.launchwrapper.Launch;

public final class FileLogger implements AutoCloseable {

    private final SimpleDateFormat dateFormat;
    private PrintStream printStream = null;

    public FileLogger(String filename) {
        this(Launch.minecraftHome, filename);
    }

    public FileLogger(File logFolder, String filename) {
        this(logFolder, filename, null);
    }

    // timeFormat "HH:mm:ss"
    public FileLogger(File logFolder, String filename, String timeFormat) {
        if (filename == null) {
            throw new IllegalStateException("FileLogger file name cannot be null!");
        }
        if (timeFormat == null) {
            dateFormat = null;
        } else {
            dateFormat = new SimpleDateFormat(timeFormat);
        }
        if (logFolder == null) {
            logFolder = new File("");
        }
        // noinspection ResultOfMethodCallIgnored
        logFolder.mkdirs();
        final File logFile = new File(logFolder, getFilename(filename));
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

    /**
     * Returns a class loader dependent file name
     */
    private static String getFilename(String filename) {
        String clName = stripClassName(FileLogger.class.getClassLoader().getClass().getName());
        if ("LaunchClassLoader".equals(clName)) {
            return filename;
        }
        final int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return filename.substring(0, dotIndex) + '_' + clName + filename.substring(dotIndex);
        } else {
            return filename;
        }
    }

    private static String stripClassName(String classname) {
        final String[] split = classname.split("\\.");
        return split[split.length - 1];
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
