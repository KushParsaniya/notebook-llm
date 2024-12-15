package dev.kush.notebookllm.repository;

import dev.kush.notebookllm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username =:username")
    Optional<User> findByUsername(String username);

    @Modifying
    @Query("UPDATE User u SET u.password =:password WHERE u.username =:username")
    int updatePasswordByUsername(String username, String password);

    @Query("SELECT coalesce(u.userId,0) FROM User u WHERE u.username =:username")
    long findUserIdByUsername(String username);

    @Query("select count(1) > 0 from User u where u.username = :username")
    boolean existsByUsername(String username);

    @Modifying
    @Query(nativeQuery = true, value = "update users set deleted =:deleted where username =:username")
    int updateDeletedByUsername(String currentUsername, boolean deleted);
}
