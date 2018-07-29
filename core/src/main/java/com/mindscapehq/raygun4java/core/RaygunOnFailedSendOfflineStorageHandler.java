package com.mindscapehq.raygun4java.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.logging.Logger;

public class RaygunOnFailedSendOfflineStorageHandler implements IRaygunOnFailedSend {

    private Random random = new Random();
    private String storageDir;
    private File storage;
    boolean enabled = true;
    private boolean exceptionLogged = false;

    public RaygunOnFailedSendOfflineStorageHandler(String storageDir) {
        this.storageDir = storageDir;
    }

    public void handle(String jsonPayload, Exception e) {
        if (!enabled) {
            return;
        }
        OutputStream fileOutputStream = null;
        try {
            File file = createFile();

            if (file == null) {
                return;
            }

            fileOutputStream = getOutputStream(file);
            fileOutputStream.write(jsonPayload.getBytes());

            exceptionLogged = false;
        } catch (Exception e1) {
            if(!exceptionLogged) {
                Logger.getLogger("Raygun4Java").warning("exception adding to offline storage: " + e1.getMessage());
                exceptionLogged = true;
            }
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e1) {
                    Logger.getLogger("Raygun4Java").warning("exception closing file stream: " + e1.getMessage());
                }
            }

        }
    }
    File createFile() {
        try {
            if (storage == null) {
                synchronized (this) {
                    if (storage == null) {
                        storage = createStorage(storageDir);

                        if (!storage.exists()) {
                            if(!storage.mkdirs()) {
                                return disable();
                            }
                        }
                    }
                }
            }

            File file = createFile(storageDir, random.nextInt()+".json");

            if (!file.createNewFile()) {
                return disable();
            }

            return file;
        } catch (Exception e) {
            return disable();
        }
    }

    OutputStream getOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    File createStorage(String storageDir) {
        return new File(storageDir);
    }

    File createFile(String storageDir, String name) {
        return new File(storageDir, name);
    }

    private File disable() {
        enabled = false;
        return null;
    }
}
