package dev.kush.notebookllm.dto;

import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomB2ContentSource implements B2ContentSource {

    private File file;

    public CustomB2ContentSource(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public long getContentLength() throws IOException {
        return file.length();
    }

    @Override
    public String getSha1OrNull() throws IOException {
        return null;
    }

    @Override
    public Long getSrcLastModifiedMillisOrNull() throws IOException {
        return file.lastModified();
    }

    @Override
    public InputStream createInputStream() throws IOException, B2Exception {
        return new FileInputStream(file);
    }
}
