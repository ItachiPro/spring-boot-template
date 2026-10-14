package com.template.templateapi.persistence.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {
    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Column(name = "deleted_at")
    private LocalDateTime deletedDate;

    @Column(name = "created_by")
    @CreatedBy
    private Long createdBy;

    @Column(name = "updated_by")
    @LastModifiedBy
    private Long modifiedBy;
}
