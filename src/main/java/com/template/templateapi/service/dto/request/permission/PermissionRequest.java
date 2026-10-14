package com.template.templateapi.service.dto.request.permission;

import com.template.templateapi.persistence.entity.PermissionEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    public PermissionEntity toEntity() {
        PermissionEntity entity = new PermissionEntity();

        entity.setName(this.name);

        return entity;
    }
}
