package com.template.templateapi.service.dto.request.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class AssignRolesRequest {
    @NotEmpty(message = "At least one role is required.")
    private List<@NonNull @Positive Long> roles;
}
