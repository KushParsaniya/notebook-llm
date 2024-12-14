package dev.kush.notebookllm.entity;

import dev.kush.notebookllm.constant.ProjectConstant;
import dev.kush.notebookllm.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.validator.constraints.Length;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SoftDelete
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userId;

    @Email(message = ProjectConstant.EMAIL_VALIDATION)
    @Column(unique = true, nullable = false)
    private String username;

    @Length(min = 8, message = ProjectConstant.PASSWORD_VALIDATION)
    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Column(name = "created_at", columnDefinition = "timestamp default current_timestamp")
    private Instant createdAt = Instant.now();

    @Column(name = "enabled", columnDefinition = "boolean default false")
    private boolean enabled;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    public User(String username, String password, String phoneNumber, UserRole role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}
