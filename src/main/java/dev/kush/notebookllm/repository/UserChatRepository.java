package dev.kush.notebookllm.repository;

import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.entity.UserChat;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserChatRepository extends CassandraRepository<UserChat, Long> {

    @Query("delete from user_chat where chat_id = :chatId and user_id = :userId")
    void deleteByChatIdAndUserId(long chatId, long userId);

    @Query("select * from user_chat where user_id =:userId")
    List<UserChat> getUserChatsByUserId(long userId);
}