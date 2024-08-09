package com.springbootstudy.sociallogin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Data
@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false, columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime updatedAt;
}
