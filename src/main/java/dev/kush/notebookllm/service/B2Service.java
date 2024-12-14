package dev.kush.notebookllm.service;

import dev.kush.notebookllm.dto.CustomB2ContentSource;

public interface B2Service {
    boolean uploadSmallFileToBucket(CustomB2ContentSource customB2ContentSource);

    boolean uploadLargeFileToBucket(CustomB2ContentSource customB2ContentSource);

    byte[] downloadFile(String fileId);


}
