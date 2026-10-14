package com.template.templateapi.service;

import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.persistence.repository.UserRepository;
import com.template.templateapi.service.dto.request.RegisterRequest;
import com.template.templateapi.web.config.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RoleService roleService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, UserService userService, RoleService roleService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleService = roleService;
    }

    public UserEntity register(RegisterRequest request){
        RoleEntity userRole = this.roleService.getRoleByName("USER");

        UserEntity user = request.toEntity();

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return this.userService.saveUser(user);
    }

    public String login(String username, String password){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        this.authenticationManager.authenticate(token);

        return this.jwtUtil.create(username);
    }

    public Map<String, Object> getAuthenticatedUserData(String username){
        UserEntity user = this.userRepository.findByUsernameWithRolesAndPermission(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Set<String> roleNames = user.getRoles().stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        Set<String> permissionNames = new HashSet<>();

        for(RoleEntity role : user.getRoles()){
            for(PermissionEntity permission : role.getPermissions()) {
                permissionNames.add(permission.getName());
            }
        }

        for(PermissionEntity permission : user.getPermissions()) {
            permissionNames.add(permission.getName());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("roles", roleNames);
        data.put("permissions", permissionNames);

        return data;
    }
}
