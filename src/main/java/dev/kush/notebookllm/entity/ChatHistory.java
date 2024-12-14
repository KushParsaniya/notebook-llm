package dev.kush.notebookllm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("chat_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

    @PrimaryKeyColumn(name = "chat_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String chatId;

    private long userId;

    private String username;

    private String message;

    private String messageType;

    @PrimaryKeyColumn(name = "created_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Instant createdAt;
}
