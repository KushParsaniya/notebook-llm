package dev.kush.notebookllm.service.impl;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.contentHandlers.B2ContentMemoryWriter;
import com.backblaze.b2.client.contentSources.B2ContentTypes;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import dev.kush.notebookllm.dto.CustomB2ContentSource;
import dev.kush.notebookllm.entity.UploadedFile;
import dev.kush.notebookllm.enums.UploadedFileStatus;
import dev.kush.notebookllm.service.B2Service;
import dev.kush.notebookllm.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class B2ServiceImpl implements B2Service {

    private static final Logger log = LoggerFactory.getLogger(B2ServiceImpl.class);
    private final B2StorageClient b2StorageClient;
    private final ExecutorService executorService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UserService userService;

    @Value("${b2.bucket.id}")
    private String bucketId;

    public B2ServiceImpl(B2StorageClient b2StorageClient, ApplicationEventPublisher applicationEventPublisher, UserService userService) {
        this.b2StorageClient = b2StorageClient;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.applicationEventPublisher = applicationEventPublisher;
        this.userService = userService;
    }

    @Override
    public boolean uploadSmallFileToBucket(CustomB2ContentSource customB2ContentSource) {
        var b2UploadFileRequest = B2UploadFileRequest
                .builder(bucketId,
                        customB2ContentSource.getFile().getName().replace(".pdf", "") + "-" + UUID.randomUUID() + ".pdf",
                        B2ContentTypes.B2_AUTO,
                        customB2ContentSource)
                .build();
        try {
            var uploadedFile = b2StorageClient.uploadSmallFile(b2UploadFileRequest);
            applicationEventPublisher.publishEvent(getUploadedFile(customB2ContentSource, uploadedFile));
            log.info("File uploaded successfully");
            return true;
        } catch (B2Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private UploadedFile getUploadedFile(CustomB2ContentSource customB2ContentSource, B2FileVersion uploadedFile) {
        return new UploadedFile(
                userService.getCurrentUserId(),
                userService.getCurrentUsername(),
                null,
                uploadedFile.getFileId(),
                uploadedFile.getContentType(),
                UploadedFileStatus.REMAINING,
                customB2ContentSource.getFile().getTotalSpace()
        );
    }

    @Override
    public boolean uploadLargeFileToBucket(CustomB2ContentSource customB2ContentSource) {
        var b2UploadFileRequest = B2UploadFileRequest
                .builder(bucketId,
                        customB2ContentSource.getFile().getName().replace(".pdf", "") + "-" + UUID.randomUUID() + ".pdf",
                        B2ContentTypes.B2_AUTO,
                        customB2ContentSource)
                .build();

        try {
            var uploadedFile = b2StorageClient.uploadLargeFile(b2UploadFileRequest, executorService);
            applicationEventPublisher.publishEvent(getUploadedFile(customB2ContentSource, uploadedFile));
            log.info("File uploaded successfully");
            return true;
        } catch (B2Exception e) {
            e.printStackTrace();
            return false;
        }
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