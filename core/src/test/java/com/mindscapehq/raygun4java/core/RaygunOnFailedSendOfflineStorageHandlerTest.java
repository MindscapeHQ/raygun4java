package com.mindscapehq.raygun4java.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunOnFailedSendOfflineStorageHandlerTest {

    private RaygunOnFailedSendOfflineStorageHandler handler;
    private final String storageDir = "storage";

    @Mock
    private File storage, file;
    @Mock
    private OutputStream outputStream;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir) {
            @Override
            File createStorage(String storageDir) {
                return storage;
            }

            @Override
            File createFile(String storageDir, String name) {
                return file;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }
        };

        when(storage.exists()).thenReturn(true);
        when(file.createNewFile()).thenReturn(true);
    }

    @Test
    public void shouldWritePayloadToFile() throws IOException {
        handler.handle("payload", null);

        verify(outputStream).write("payload".getBytes());
        verify(outputStream).close();
    }

    @Test
    public void shouldNotWritePayloadToFileIfDisabled() throws IOException {
        handler.enabled = false;
        handler.handle("payload", null);

        verify(outputStream, never()).write((byte[]) anyObject());
    }

    @Test
    public void shouldDisableIfCanMakeStorageDirs() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir) {
            @Override
            File createStorage(String storageDir) {
                return storage;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }
        };

        when(storage.exists()).thenReturn(false);
        when(storage.mkdirs()).thenReturn(false);

        handler.handle("payload", null);

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }

    @Test
    public void shouldDisableIfCanMakeFile() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir) {
            @Override
            File createStorage(String storageDir) {
                return storage;
            }

            @Override
            OutputStream getOutputStream(File file) throws FileNotFoundException {
                return outputStream;
            }

            @Override
            File createFile(String storageDir, String name) {
                return file;
            }
        };

        when(file.createNewFile()).thenReturn(false);

        handler.handle("payload", null);

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }

    @Test
    public void shouldDisableOnFileCreateException() throws IOException {
        handler = new RaygunOnFailedSendOfflineStorageHandler(storageDir) {
            @Override
            File createStorage(String storageDir) {
                throw new RuntimeException();
            }
        };

        when(file.createNewFile()).thenThrow(new RuntimeException());

        handler.handle("payload", null);

        verify(outputStream, never()).write((byte[]) anyObject());
        assertFalse(handler.enabled);
    }
}