package com.template.templateapi.service.dto.response.role;

import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private List<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RoleResponse fromEntity(RoleEntity role){
        RoleResponse response = new RoleResponse();

        response.setId(role.getId());
        response.setName(role.getName());
        response.setPermissions(role.getPermissions().stream().map(PermissionEntity::getName).toList());
        response.setCreatedAt(role.getCreatedDate());
        response.setUpdatedAt(role.getModifiedDate());

        return response;
    }
}
