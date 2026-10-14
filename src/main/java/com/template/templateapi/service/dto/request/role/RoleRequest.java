package com.template.templateapi.service.dto.request.role;

import com.template.templateapi.persistence.entity.RoleEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank(message = "Name is required.")
    private String name;

    public RoleEntity toEntity() {
        RoleEntity role = new RoleEntity();

        role.setName(this.name);

        return role;
    }
}
