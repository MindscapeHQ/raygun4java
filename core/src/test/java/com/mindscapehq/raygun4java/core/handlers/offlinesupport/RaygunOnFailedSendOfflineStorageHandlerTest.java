package com.mindscapehq.raygun4java.core.handlers.offlinesupport;

import com.mindscapehq.raygun4java.core.RaygunClient;
import com.mindscapehq.raygun4java.core.messages.RaygunMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunOnFailedSendOfflineStorageHandlerTest {

    private RaygunOnFailedSendOfflineStorageHandler handler;
    private final String storageDir = "storage";

    @Mock
    private File storage, file;
    @Mock
    private OutputStream outputStream;
    @Mock
    private RaygunClient client;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        final File storageMock = storage;
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "apiKey") {
            @Override
            File getStorage(String storageDir) {
                return storageMock;
            }

            @Override
            File getFile(File storage, String name) {
                return file;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }
        };

        when(storage.exists()).thenReturn(true);
        when(storage.isDirectory()).thenReturn(true);
        when(file.createNewFile()).thenReturn(true);
    }

    @Test
    public void shouldDefaultToHasStoredExceptionsSoThatSendsOnFirstError() throws InterruptedException {
        assertTrue(handler.hasStoredExceptions);
        handler.onBeforeSend(client, null);
    }

    @Test
    public void shouldWritePayloadToFile() throws IOException {
        handler.onFailedSend(null, "payload");

        verify(outputStream).write("payload".getBytes());
        verify(outputStream).close();

        assertTrue(handler.hasStoredExceptions);
    }

    @Test
    public void shouldNotWritePayloadToFileIfDisabled() throws IOException {
        handler.enabled = false;
        handler.onFailedSend(null, "payload");

        verify(outputStream, never()).write((byte[]) anyObject());
    }

    @Test
    public void shouldDisableIfCanMakeStorageDirs() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "apiKey") {
            @Override
            File getStorage(String storageDir) {
                return storage;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }
        };

        when(storage.exists()).thenReturn(false);
        when(storage.mkdirs()).thenReturn(false);

        handler.onFailedSend(null, "payload");

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }

    @Test
    public void shouldDisableIfCantMakeFile() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "apiKey") {
            @Override
            File getStorage(String storageDir) {
                return storage;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }

            @Override
            File getFile(File storage, String name) {
                return file;
            }
        };

        when(file.createNewFile()).thenReturn(false);

        handler.onFailedSend(null, "payload");

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }

    @Test
    public void shouldDisableOnFileCreateException() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "apiKey") {
            @Override
            File getStorage(String storageDir) {
                throw new RuntimeException();
            }
        };

        when(file.createNewFile()).thenThrow(new RuntimeException());

        handler.onFailedSend(null, "payload");

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }

    @Test
    public void shouldSendStoredExceptionsOnce() throws InterruptedException {
        handler.enabled = true;
        handler.hasStoredExceptions = true;
        RaygunMessage message = mock(RaygunMessage.class);
        when(storage.listFiles((FilenameFilter) anyObject())).thenReturn(new File[0]);
        handler.storage = storage;

        assertThat(handler.onBeforeSend(client, message), is(message));

        Thread.sleep(200);
        verify(storage, times(2)).listFiles((FilenameFilter) anyObject());

        assertThat(handler.onBeforeSend(client, message), is(message));
        Thread.sleep(200);
        verify(storage, times(2)).listFiles((FilenameFilter) anyObject());
    }

    @Test
    public void shouldCreateDifferentStorageForDifferentApiKeys() {
        String s1 = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "myApiKey").getStorage("").getName();
        String s2 = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "myApiKey").getStorage("").getName();
        assertEquals(s1, ".raygun_offline_storage_lxertb");
        assertEquals(s2, s2);

        String s3 = new RaygunOnFailedSendOfflineStorageHandler(storageDir, "notMyApiKey").getStorage("").getName();
        assertNotEquals(s1, s3);
    }
}