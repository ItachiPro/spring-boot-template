package com.template.templateapi.persistence.entity;

import com.template.templateapi.persistence.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE permissions SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class PermissionEntity extends AuditableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
