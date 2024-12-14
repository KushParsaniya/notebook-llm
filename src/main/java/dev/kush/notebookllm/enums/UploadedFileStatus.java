package dev.kush.notebookllm.enums;

import lombok.Getter;

@Getter
public enum UploadedFileStatus {

    REMAINING("remaining"),
    PROCESSING("processing"),
    FINISHED("finished");

    private final String value;

    UploadedFileStatus(String value) {
        this.value = value;
    }
}
