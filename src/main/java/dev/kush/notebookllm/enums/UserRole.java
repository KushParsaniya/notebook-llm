package dev.kush.notebookllm.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("SCOPE_user"),
    ADMIN("SCOPE_admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }
}
