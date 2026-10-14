package com.template.templateapi.service.dto.response.user;

import com.template.templateapi.persistence.entity.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse fromEntity(UserEntity user){
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedDate());
        response.setUpdatedAt(user.getModifiedDate());

        return response;
    }
}
