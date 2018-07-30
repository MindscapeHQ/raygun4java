package com.mindscapehq.raygun4java.core.handlers.offlinesupport;

import com.mindscapehq.raygun4java.core.RaygunClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RaygunSendStoredExceptionsTest {

    @Mock
    RaygunClient client;
    @Mock
    File storage, file;

    InputStream inputStream;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        File[] files = { file };
        when(file.length()).thenReturn((long) "hello world".length());
        when(storage.listFiles((FilenameFilter) anyObject())).thenReturn(files);
        when(client.send(anyString())).thenReturn(202);

        inputStream = spy(new ByteArrayInputStream("hello world".getBytes()));
    }

    @Test
    public void processFilesShouldProcessRaygunFilesOnly() throws IOException {
        RaygunSendStoredExceptions sendStoredExceptions = new RaygunSendStoredExceptions(client, storage) {
            @Override
            InputStream getInputStream(File file) {
                return inputStream;
            }
        };

        sendStoredExceptions.processFiles();

        verify(client).send("hello world");
        verify(inputStream, times(2)).close();
    }

}