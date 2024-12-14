package dev.kush.notebookllm.service.impl;

import dev.kush.notebookllm.entity.UploadedFile;
import dev.kush.notebookllm.repository.UploadedFileRepository;
import dev.kush.notebookllm.service.UploadedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    @Override
    public UploadedFile save(UploadedFile uploadedFile) {
        return uploadedFileRepository.save(uploadedFile);
    }

    @Override
    @Transactional
    public void updateIsProcessedByIdAndFileId(long id, String fileId, String status) {
        uploadedFileRepository.updateIsProcessedByIdAndFileId(id, fileId, status);
    }

    @Override
    public List<UploadedFile> getUploadedFilesByStatus(String status, long offset, long limit) {
        return uploadedFileRepository.getUploadedFileByStatus(status, offset, limit);
    }

    @Override
    public long countUploadedFilesByStatus(String status) {
        return uploadedFileRepository.countUploadedFilesByStatus(status);
    }
}
