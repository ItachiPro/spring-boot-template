package com.template.templateapi.service.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class AssignPermissionRequest {
    @NotEmpty(message = "At least one permission is required.")
    private List<@NotNull @Positive Long> permissions;
}
