package dev.kush.notebookllm.enums;

import lombok.Getter;

@Getter
public enum UploadedFileStatus {

    REMAINING("REMAINING"),
    PROCESSING("PROCESSING"),
    FINISHED("FINISHED");

    private final String value;

    UploadedFileStatus(String value) {
        this.value = value;
    }
}
