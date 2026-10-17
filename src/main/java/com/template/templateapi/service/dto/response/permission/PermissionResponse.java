package com.template.templateapi.service.dto.response.permission;

import com.template.templateapi.persistence.entity.PermissionEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PermissionResponse {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PermissionResponse fromEntity(PermissionEntity permission) {
        PermissionResponse response = new PermissionResponse();

        response.setId(permission.getId());
        response.setName(permission.getName());
        response.setCreatedAt(permission.getCreatedDate());
        response.setUpdatedAt(permission.getModifiedDate());

        return response;
    }
}
