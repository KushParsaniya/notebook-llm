package dev.kush.notebookllm.service.impl;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentHandlers.B2ContentMemoryWriter;
import com.backblaze.b2.client.contentSources.B2ContentTypes;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.dto.CustomB2ContentSource;
import dev.kush.notebookllm.entity.UploadedFile;
import dev.kush.notebookllm.enums.UploadedFileStatus;
import dev.kush.notebookllm.service.B2Service;
import dev.kush.notebookllm.service.UploadedFileService;
import dev.kush.notebookllm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class B2ServiceImpl implements B2Service {

    private static final Logger log = LoggerFactory.getLogger(B2ServiceImpl.class);
    private final B2StorageClient b2StorageClient;
    private final ExecutorService executorService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserService userService;
    private final UploadedFileService uploadedFileService;
    private final ObjectMapper objectMapper;

    @Value("${b2.bucket.id}")
    private String bucketId;

    @Override
    public UserController.UserUploadedFileDto uploadSmallFileToBucket(CustomB2ContentSource customB2ContentSource) {
        var b2UploadFileRequest = B2UploadFileRequest
                .builder(bucketId,
                        customB2ContentSource.getFile().getName().replace(".pdf", "") + "-" + UUID.randomUUID() + ".pdf",
                        B2ContentTypes.B2_AUTO,
                        customB2ContentSource)
                .build();
        try {
            var b2FileVersion = b2StorageClient.uploadSmallFile(b2UploadFileRequest);
            var uploadedFile = getUploadedFile(customB2ContentSource, b2FileVersion);
            uploadedFile = uploadedFileService.save(uploadedFile);
            applicationEventPublisher.publishEvent(uploadedFile);
            log.info("File uploaded successfully");
            return getUserUploadedFileDto(uploadedFile);
        } catch (B2Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private UploadedFile getUploadedFile(CustomB2ContentSource customB2ContentSource, B2FileVersion uploadedFile) {
        return new UploadedFile(
                userService.getCurrentUserId(),
                userService.getCurrentUsername(),
                null,
                uploadedFile.getFileId(),
                customB2ContentSource.getFile().getName(),
                uploadedFile.getContentType(),
                UploadedFileStatus.REMAINING,
                customB2ContentSource.getFile().getTotalSpace()
        );
    }

    @Override
    public UserController.UserUploadedFileDto uploadLargeFileToBucket(CustomB2ContentSource customB2ContentSource) {
        var b2UploadFileRequest = B2UploadFileRequest
                .builder(bucketId,
                        customB2ContentSource.getFile().getName().replace(".pdf", "") + "-" + UUID.randomUUID() + ".pdf",
                        B2ContentTypes.B2_AUTO,
                        customB2ContentSource)
                .build();

        try {
            var b2FileVersion = b2StorageClient.uploadLargeFile(b2UploadFileRequest, executorService);
            var uploadedFile = getUploadedFile(customB2ContentSource, b2FileVersion);
            uploadedFile = uploadedFileService.save(uploadedFile);
            applicationEventPublisher.publishEvent(uploadedFile);
            log.info("File uploaded successfully");
            return getUserUploadedFileDto(uploadedFile);
        } catch (B2Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private UserController.UserUploadedFileDto getUserUploadedFileDto(UploadedFile uploadedFile) {
        return objectMapper.convertValue(uploadedFile, UserController.UserUploadedFileDto.class);
    }

    @Override
    public byte[] downloadFile(String fileId) {
        try {
            B2ContentMemoryWriter handler = B2ContentMemoryWriter.builder().build();
            b2StorageClient.downloadById(fileId, handler);
            return handler.getBytes();
        } catch (B2Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}