package com.template.templateapi.web.controller;

import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.service.RoleService;
import com.template.templateapi.service.dto.request.AssignPermissionRequest;
import com.template.templateapi.service.dto.request.role.RoleRequest;
import com.template.templateapi.service.dto.response.role.RoleResponse;
import com.template.templateapi.web.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if(page == null || size == null){
            List<RoleResponse> roles = this.roleService.getAll()
                    .stream()
                    .map(RoleResponse::fromEntity)
                    .toList();
            return ApiResponse.success(roles, "Roles found.");
        }

        Pageable pageRequest = PageRequest.of(page, size);
        Page<RoleResponse> roles = this.roleService.getAll(pageRequest)
                .map(RoleResponse::fromEntity);
        return ApiResponse.success(roles, "Roles found.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(@PathVariable Long id){
        RoleEntity role = this.roleService.getRoleById(id);
        return ApiResponse.success(RoleResponse.fromEntity(role), "Role found.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> save(@Valid @RequestBody RoleRequest request){
        RoleEntity saved = this.roleService.saveRole(request.toEntity());

        return ApiResponse.success(RoleResponse.fromEntity(saved), "Role created successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@Valid @RequestBody RoleRequest request, @PathVariable Long id){
        RoleEntity saved = this.roleService.updateRole(id, request);

        return ApiResponse.success(RoleResponse.fromEntity(saved), "Role updated successfully.");
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermission(@Valid @RequestBody AssignPermissionRequest request, @PathVariable Long id){
        RoleEntity saved = this.roleService.assignPermission(id, request.getPermissions());

        return ApiResponse.success(RoleResponse.fromEntity(saved), "Permissions assigned successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.roleService.deleteRole(id);

        return ApiResponse.deleted();
    }
}
