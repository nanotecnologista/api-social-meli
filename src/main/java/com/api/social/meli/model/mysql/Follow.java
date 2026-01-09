package com.api.social.meli.model.mysql;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "followers",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_follower_user_seller",
                columnNames = {"user_id", "seller_id"}
        ),
        indexes = {
                @Index(name = "idx_follower_user", columnList = "user_id"),
                @Index(name = "idx_follower_seller", columnList = "seller_id")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // quem segue

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller; // quem é seguido

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}