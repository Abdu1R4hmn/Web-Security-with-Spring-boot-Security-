package com.example.demo.auth.refreshToken;

import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "tokenHash"),
                @Index(name = "idx_refresh_token_user", columnList = "user_id")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column
    private Instant lastUsedAt;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @PrePersist
    protected void createdAt(){
        this.createdAt = Instant.now();
    }

}
