package com.template.templateapi.web.controller;

import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.service.PermissionService;
import com.template.templateapi.service.dto.request.permission.PermissionRequest;
import com.template.templateapi.service.dto.response.permission.PermissionResponse;
import com.template.templateapi.web.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if(page == null || size == null){
            List<PermissionResponse> permissions = this.permissionService.getAll()
                    .stream()
                    .map(PermissionResponse::fromEntity)
                    .toList();
            return ApiResponse.success(permissions, "Permissions found.");
        }

        Pageable pageRequest = PageRequest.of(page, size);
        Page<PermissionResponse> permissions = this.permissionService.getAll(pageRequest)
                .map(PermissionResponse::fromEntity);
        return ApiResponse.success(permissions, "Permissions found.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> getById(@PathVariable Long id) {
        PermissionEntity permission = this.permissionService.getPermissionById(id);

        return ApiResponse.success(PermissionResponse.fromEntity(permission), "Permission found.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> save(@Valid @RequestBody PermissionRequest request) {
        PermissionEntity saved = this.permissionService.savePermission(request.toEntity());

        return ApiResponse.success(PermissionResponse.fromEntity(saved), "Permission created successfully.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(@Valid @RequestBody PermissionRequest request, @PathVariable Long id) {
        PermissionEntity saved = this.permissionService.updatePermission(id, request);

        return ApiResponse.success(PermissionResponse.fromEntity(saved), "Permission updated successfully.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.permissionService.deletePermission(id);

        return ResponseEntity.ok().build();
    }
}
