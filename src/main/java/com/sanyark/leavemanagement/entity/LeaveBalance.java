package com.sanyark.leavemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "leave_balances",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_leaves", nullable = false)
    private Integer totalLeaves;

    @Column(name = "used_leaves", nullable = false)
    private Integer usedLeaves;

    @Column(name = "pending_leaves", nullable = false)
    private Integer pendingLeaves;

    @Column(name = "remaining_leaves", nullable = false)
    private Integer remainingLeaves;

    @Column(name = "maternity_total", nullable = false)
    private Integer maternityTotal;

    @Column(name = "maternity_used", nullable = false)
    private Integer maternityUsed;

    @Column(name = "maternity_pending", nullable = false)
    private Integer maternityPending;

    @Column(name = "maternity_remaining", nullable = false)
    private Integer maternityRemaining;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}