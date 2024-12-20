package dev.kush.notebookllm.service;

import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.dto.CustomB2ContentSource;

public interface B2Service {
    UserController.UserUploadedFileDto uploadSmallFileToBucket(CustomB2ContentSource customB2ContentSource);

    UserController.UserUploadedFileDto uploadLargeFileToBucket(CustomB2ContentSource customB2ContentSource);

    byte[] downloadFile(String fileId);


}
