package dev.kush.notebookllm.controller;

import dev.kush.notebookllm.dto.CustomB2ContentSource;
import dev.kush.notebookllm.service.B2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class UploadFileController {

    private final B2Service b2Service;

    @PostMapping("/upload-small-file")
    public ResponseEntity<Boolean> uploadFile(@RequestParam("multipartFile") MultipartFile multipartFile) {
        File file = convertMultiPartToFile(multipartFile);
        if (file == null) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        var status = b2Service.uploadSmallFileToBucket(new CustomB2ContentSource(file));
        if (status) {
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/upload-large-file")
    public ResponseEntity<Boolean> uploadLargeFile(@RequestParam("file") MultipartFile multipartFile) {
        File file = convertMultiPartToFile(multipartFile);
        if (file == null) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        var status = b2Service.uploadLargeFileToBucket(new CustomB2ContentSource(file));
        if (status) {
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private File convertMultiPartToFile(MultipartFile multipartFile) {
        try {
            File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
