package com.mindscapehq.raygun4java.core.handlers.offlinesupport;

import com.mindscapehq.raygun4java.core.IRaygunOnBeforeSend;
import com.mindscapehq.raygun4java.core.IRaygunOnFailedSend;
import com.mindscapehq.raygun4java.core.IRaygunSendEventFactory;
import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.logging.Logger;

/**
 * When a send failure occurs, this class attempts to write the payload to disk
 * When a send success occurs, it resends any stored payloads
 */
public class RaygunOnFailedSendOfflineStorageHandler implements IRaygunOnFailedSend, IRaygunOnBeforeSend, IRaygunSendEventFactory {

    static final String fileExtension = ".raygunpayload";
    private Random random = new Random();
    private String storageDir;
    File storage;
    boolean enabled = true;
    private boolean exceptionLogged = false;
    boolean hasStoredExceptions = false;
    private volatile Runnable sendingStoredExceptions;

    public RaygunOnFailedSendOfflineStorageHandler(String storageDir) {
        this.storageDir = storageDir;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RaygunMessage onBeforeSend(RaygunClient client, RaygunMessage message) {
        if (enabled && hasStoredExceptions && sendingStoredExceptions == null) {
            // begin sending
            synchronized (this) {
                if (sendingStoredExceptions == null) {
                    sendingStoredExceptions = new RaygunSendStoredExceptions(client, storage);
                    new Thread(new Runnable() {
                        public void run() {
                            sendingStoredExceptions.run();
                            sendingStoredExceptions = null;
                            hasStoredExceptions = false;
                        }
                    }).start();
                }
            }
        }

        return message;
    }

    public String onFailedSend(RaygunClient client, String jsonPayload) {
        if (!enabled) {
            return jsonPayload;
        }

        OutputStream fileOutputStream = null;
        try {
            File file = createFile();

            if (file == null) {
                return jsonPayload;
            }

            fileOutputStream = getOutputStream(file);
            fileOutputStream.write(jsonPayload.getBytes());

            exceptionLogged = false;
            hasStoredExceptions = true;
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

        return jsonPayload;
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

            File file = createFile(storage, random.nextInt()+ fileExtension);

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
        return new File(new File(storageDir).getAbsolutePath(), ".raygun_offline_storage").getAbsoluteFile();
    }

    File createFile(File storage, String name) {
        return new File(storage, name);
    }

    private File disable() {
        enabled = false;
        return null;
    }

    public IRaygunOnBeforeSend create() {
        return this;
    }
}
