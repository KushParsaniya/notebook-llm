package dev.kush.notebookllm.repository;

import dev.kush.notebookllm.entity.ChatHistory;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends CassandraRepository<ChatHistory, String> {

    @Query("SELECT * FROM chat_history WHERE chat_id = :conversationId LIMIT :lastN")
    List<ChatHistory> findByChatId(String conversationId, int lastN);
}
