package dev.kush.notebookllm.repository;

import dev.kush.notebookllm.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "update uploaded_files uf set uf.status =:status where uf.id =:id and uf.file_id =:fileId")
    void updateIsProcessedByIdAndFileId(long id, String fileId, String status);

    @Query(nativeQuery = true, value = "select * from uploaded_files where status =:status offset :offset limit :limit")
    List<UploadedFile> getUploadedFileByStatus(String status, long offset, long limit);

    @Query(nativeQuery = true,value = "select count(1) from uploaded_files where status =:status")
    long countUploadedFilesByStatus(String status);
}
