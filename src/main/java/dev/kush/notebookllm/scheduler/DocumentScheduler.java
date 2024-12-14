package dev.kush.notebookllm.scheduler;

import dev.kush.notebookllm.enums.UploadedFileStatus;
import dev.kush.notebookllm.service.DocumentService;
import dev.kush.notebookllm.service.UploadedFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentScheduler {

    private final UploadedFileService uploadedFileService;
    private final DocumentService documentService;
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();



    @Scheduled(cron = "0 0 */3 * * *")
    public void processDocument() {
        log.info("processDocument scheduler start");
        final var status = UploadedFileStatus.REMAINING.getValue();
        var total = uploadedFileService.countUploadedFilesByStatus(status);
        log.info("DocumentScheduler :: processDocument :: total files: {}", total);
        if (total == 0) {
            return;
        }
        long limit = 50;
        long offset = 0;

        while (offset <= total) {
            var uploadedFiles = uploadedFileService.getUploadedFilesByStatus(status, offset, limit);
            for (var uploadedFile : uploadedFiles) {
                executorService.submit(() -> documentService.processDocument(uploadedFile.getId(), uploadedFile.getFileId()
                        , uploadedFile.getUserId(), uploadedFile.getUsername()));
            }
            offset += limit;
        }
        log.info("processDocument scheduler end.");
    }
}
