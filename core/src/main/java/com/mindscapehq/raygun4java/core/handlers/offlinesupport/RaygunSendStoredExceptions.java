package com.mindscapehq.raygun4java.core.handlers.offlinesupport;

import com.mindscapehq.raygun4java.core.RaygunClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class RaygunSendStoredExceptions implements Runnable {
    private final RaygunClient client;
    private final File storage;
    private static final Object globalSendLock = new Object();

    private static final Set<Integer> deleteOnStatusCodes = new HashSet<Integer>(Arrays.asList(202, 429, 400, 403, 413));

    public RaygunSendStoredExceptions(RaygunClient client, File storage) {
        this.client = client;
        this.storage = storage;
    }

    public void run() {
        if (storage == null || !storage.isDirectory()) {
            // on first send of errors, the offline will be checked but may not have anything to send
            return;
        }

        // ensure that only one process can do that at any time
        synchronized (globalSendLock) {
            processFiles();

            // one final time just in case some more have arrived since then
            processFiles();
        }
    }

    void processFiles() {

        File[] files = storage.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(RaygunOnFailedSendOfflineStorageHandler.fileExtension);
            }
        });

        if (files == null) {
            return;
        }

        for (File file : files) {
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = getInputStream(file);
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[(int) file.length()];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();

                int reponseCode = client.send(outputStream.toString("UTF-8"));
                if (deleteOnStatusCodes.contains(reponseCode)) {
                    file.delete();
                }
            } catch (IOException e) {
                Logger.getLogger("Raygun4Java").warning("exception processing offline payload: " + e.getMessage());
                return;
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        Logger.getLogger("Raygun4Java").warning("exception closing outputStream: " + e.getMessage());
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Logger.getLogger("Raygun4Java").warning("exception closing inputStream: " + e.getMessage());
                    }
                }
            }
        }
    }

    InputStream getInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }
}
