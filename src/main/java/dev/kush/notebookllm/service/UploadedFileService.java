package dev.kush.notebookllm.service;

import dev.kush.notebookllm.entity.UploadedFile;

import java.util.List;

public interface UploadedFileService {
    UploadedFile save(UploadedFile uploadedFile);

    void updateIsProcessedByIdAndFileId(long id, String fileId, String status);

    List<UploadedFile> getUploadedFilesByStatus(String status, long offset, long limit);

    long countUploadedFilesByStatus(String status);
}
