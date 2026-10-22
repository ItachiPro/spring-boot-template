package com.template.templateapi.service;

import com.template.templateapi.common.RoleNames;
import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.persistence.repository.PermissionRepository;
import com.template.templateapi.persistence.repository.RoleRepository;
import com.template.templateapi.persistence.repository.UserRepository;
import com.template.templateapi.service.dto.request.user.UpdateUserRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long getIdByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .map(UserEntity::getId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public List<UserEntity> getAll(Long currentId){
        return this.userRepository.findAllVisible(currentId, RoleNames.SUPER_ADMIN);
    }

    public Page<UserEntity> getAll(Long currentId, Pageable pageable){
        return this.userRepository.findVisibleUsers(currentId, RoleNames.SUPER_ADMIN, pageable);
    }

    public UserEntity getUserById(Long id, Long currentId){
        if(Objects.equals(id, currentId)){
            throw new EntityNotFoundException("User not found.");
        }

        return findVisibleOrFail(id);
    }

    public UserEntity getUserByEmail(String email){
        return this.userRepository.findVisibleByEmail(email, RoleNames.SUPER_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public UserEntity getUserByUsername(String username){
        return this.userRepository.findVisibleByUsername(username, RoleNames.SUPER_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public UserEntity saveUser(UserEntity userEntity){
        if(this.userRepository.existsByUsername(userEntity.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }

        if(this.userRepository.existsByEmail(userEntity.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        return this.userRepository.save(userEntity);
    }

    public UserEntity updateUser(Long id, UpdateUserRequest request){
        UserEntity user = findVisibleOrFail(id);

        if(!Objects.equals(user.getUsername(), request.getUsername()) && this.userRepository.existsByUsername(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }

        if(!Objects.equals(user.getEmail(), request.getEmail()) && this.userRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        return this.userRepository.save(user);
    }

    public UserEntity assignRoles(Long id, List<Long> roleIds, Long currentId){
        preventSelfAction(id, currentId);

        UserEntity user = findVisibleOrFail(id);

        Set<Long> uniqueIds = new HashSet<>(roleIds);
        List<RoleEntity> foundRoles = this.roleRepository.findAllById(uniqueIds);

        if(foundRoles.size() != uniqueIds.size()){
            throw new EntityNotFoundException("One or more roles not found.");
        }

        if(foundRoles.stream().anyMatch(r -> RoleNames.SUPER_ADMIN.equals(r.getName()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This role cannot be assigned.");
        }

        user.setRoles(new HashSet<>(foundRoles));

        return this.userRepository.save(user);
    }

    public UserEntity assignPermission(Long id, List<Long> permissionIds, Long currentId){
        preventSelfAction(id, currentId);

        UserEntity user = findVisibleOrFail(id);

        Set<Long> uniqueIds = new HashSet<>(permissionIds);
        List<PermissionEntity> foundPermissions = this.permissionRepository.findAllById(uniqueIds);

        if(foundPermissions.size() != uniqueIds.size()){
            throw new EntityNotFoundException("One or more permissions not found.");
        }

        user.setPermissions(new HashSet<>(foundPermissions));

        return this.userRepository.save(user);
    }

    public void deleteUser(Long id, Long currentId){
        preventSelfAction(id, currentId);
        UserEntity user = findVisibleOrFail(id);
        this.userRepository.delete(user);
    }

    private UserEntity findVisibleOrFail(Long id){
        return this.userRepository.findVisibleById(id, RoleNames.SUPER_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    private void preventSelfAction(Long id, Long currentId){
        if(Objects.equals(id, currentId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot perform this action on your own user.");
        }
    }
}
