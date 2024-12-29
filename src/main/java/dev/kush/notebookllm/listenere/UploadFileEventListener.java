package dev.kush.notebookllm.listenere;

import dev.kush.notebookllm.entity.UploadedFile;
import dev.kush.notebookllm.service.DocumentService;
import dev.kush.notebookllm.service.UploadedFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UploadFileEventListener {

    private final UploadedFileService uploadedFileService;
    private final DocumentService documentService;

    @EventListener(UploadedFile.class)
    public void onFileUpload(UploadedFile uploadedFile) {
        // save the uploaded file
        if (uploadedFile == null) {
            log.error("UploadFileEventListener :: onFileUpload :: uploadedFile is null");
        }
        // process the uploaded file
        documentService.processDocument(uploadedFile.getId(), uploadedFile.getFileId(), uploadedFile.getUserId(), uploadedFile.getUsername());
    }
}
