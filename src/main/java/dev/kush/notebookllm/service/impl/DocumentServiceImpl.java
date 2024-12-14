package dev.kush.notebookllm.service.impl;

import dev.kush.notebookllm.enums.UploadedFileStatus;
import dev.kush.notebookllm.service.B2Service;
import dev.kush.notebookllm.service.DocumentService;
import dev.kush.notebookllm.service.UploadedFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final VectorStore vectorStore;
    private final B2Service b2Service;
    private final UploadedFileService uploadedFileService;

    @Override
    public void processDocument(long id, String fileId, long userId, String username) {
        try {
            // TODO: use file format to process different type of files
            log.info("DocumentService :: processDocument :: processing start for fileId {}", fileId);
            uploadedFileService.updateIsProcessedByIdAndFileId(id, fileId, UploadedFileStatus.PROCESSING.getValue());
            var fileContent = b2Service.downloadFile(fileId);
            Resource resource = new ByteArrayResource(fileContent);
            ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(resource,
                    PdfDocumentReaderConfig.builder()
                            .withPageTopMargin(0)
                            .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                    .withNumberOfTopTextLinesToDelete(0)
                                    .build())
                            .withPagesPerDocument(1)
                            .build());
            List<Document> documents = pdfReader.read();
            documents.forEach(document -> document
                    .getMetadata()
                    .putAll(Map.of("userId", userId, "username", username))
            );
            vectorStore.accept(documents);
            uploadedFileService.updateIsProcessedByIdAndFileId(id, fileId, UploadedFileStatus.FINISHED.getValue());
            log.info("DocumentService :: processDocument :: processing end for fileId {}", fileId);
        } catch (Exception e) {
            log.info("DocumentService :: processDocument :: error for fileId {}", fileId);
            uploadedFileService.updateIsProcessedByIdAndFileId(id, fileId, UploadedFileStatus.REMAINING.getValue());
        }
    }
}
