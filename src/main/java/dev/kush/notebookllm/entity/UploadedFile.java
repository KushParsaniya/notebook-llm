package dev.kush.notebookllm.entity;

import dev.kush.notebookllm.enums.UploadedFileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SoftDelete
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    private String username;

    private String uri;

    private String fileId;

    private String contentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_processed", columnDefinition = "text check (is_processed in ('REMAINING','PROCESSING','FINISHED'))")
    private UploadedFileStatus status;

    private long size;

    public UploadedFile(long userId, String username, String uri, String fileId, String contentType,UploadedFileStatus status, long size) {
        this.userId = userId;
        this.username = username;
        this.uri = uri;
        this.fileId = fileId;
        this.contentType = contentType;
        this.status = status;
        this.size = size;
    }
}
