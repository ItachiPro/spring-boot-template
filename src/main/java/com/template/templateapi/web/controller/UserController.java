package com.template.templateapi.web.controller;

import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.service.UserService;
import com.template.templateapi.service.dto.request.AssignPermissionRequest;
import com.template.templateapi.service.dto.request.user.AssignRolesRequest;
import com.template.templateapi.service.dto.request.user.CreateUserRequest;
import com.template.templateapi.service.dto.request.user.UpdateUserRequest;
import com.template.templateapi.service.dto.response.user.UserResponse;
import com.template.templateapi.web.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            Authentication authentication,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Long currentId = this.userService.getIdByUsername(authentication.getName());

        if(page == null || size == null){
            List<UserResponse> users = this.userService.getAll(currentId)
                    .stream()
                    .map(UserResponse::fromEntity)
                    .toList();
            return ApiResponse.success(users, "Users found.");
        }

        Pageable pageRequest = PageRequest.of(page, size);
        Page<UserResponse> users = this.userService.getAll(currentId, pageRequest)
                .map(UserResponse::fromEntity);
        return ApiResponse.success(users, "Users found.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id){
        UserEntity user = this.userService.getUserById(id);
        return ApiResponse.success(UserResponse.fromEntity(user), "User found.");
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserResponse>> getByUsernameOrEmail(@RequestParam(required = false) String username, @RequestParam(required = false) String email){
        if(username != null){
            return ApiResponse.success(UserResponse.fromEntity(this.userService.getUserByUsername(username)), "User found.");
        }

        if(email != null){
            return ApiResponse.success(UserResponse.fromEntity(this.userService.getUserByEmail(email)), "User found.");
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email is required.");
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> save(@Valid @RequestBody CreateUserRequest request){
        UserEntity saved = this.userService.saveUser(request.toEntity());
        return ApiResponse.success(UserResponse.fromEntity(saved), "User created successfully.", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@Valid @RequestBody UpdateUserRequest request, @PathVariable Long id){
        UserEntity saved = this.userService.updateUser(id, request);

        return ApiResponse.success(UserResponse.fromEntity(saved), "User updated successfully.", HttpStatus.OK);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> assignRoles(@Valid @RequestBody AssignRolesRequest request, @PathVariable Long id, @AuthenticationPrincipal Long currentId){
        UserEntity saved = this.userService.assignRoles(id, request.getRoles(), currentId);

        return ApiResponse.success(UserResponse.fromEntity(saved), "Roles assigned successfully.", HttpStatus.OK);
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<ApiResponse<UserResponse>> assignPermission(@Valid @RequestBody AssignPermissionRequest request, @PathVariable Long id, @AuthenticationPrincipal Long currentId){
        UserEntity saved = this.userService.assignPermission(id, request.getPermissions(), currentId);

        return ApiResponse.success(UserResponse.fromEntity(saved), "Permissions assigned successfully.", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Long currentId){
        this.userService.deleteUser(id,  currentId);

        return ApiResponse.deleted();
    }
}
