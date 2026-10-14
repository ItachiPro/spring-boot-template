package com.template.templateapi.service.dto.request.user;

import com.template.templateapi.persistence.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username is required.")
    @Size(min = 4, message = "Username must be a least 4 characters.")
    @Size(max = 20, message = "Username must be a least 20 characters.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be a least 8 characters.")
    private String password;

    public UserEntity toEntity(){
        UserEntity user = new UserEntity();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        return user;
    }
}
