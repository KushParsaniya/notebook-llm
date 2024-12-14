package dev.kush.notebookllm.service;

public interface DocumentService {
    void processDocument(long id,String fileId, long userId, String username);
}
