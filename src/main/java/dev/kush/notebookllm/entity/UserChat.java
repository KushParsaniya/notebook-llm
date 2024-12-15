package dev.kush.notebookllm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Table("user_chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserChat {

    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private long userId;

    private String username;

    private String title;

    @PrimaryKeyColumn(name = "chat_id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String chatId;

    @PrimaryKeyColumn(name = "created_at", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    @CassandraType(type = CassandraType.Name.TIMESTAMP)
    private Instant createdAt = Instant.now();

    public UserChat(long userId, String username, String title, String chatId) {
        this.userId = userId;
        this.username = username;
        this.title = title;
        this.chatId = chatId;
    }
}
