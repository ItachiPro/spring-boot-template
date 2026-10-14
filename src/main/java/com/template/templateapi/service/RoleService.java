package com.template.templateapi.service;

import com.template.templateapi.common.RoleNames;
import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.repository.PermissionRepository;
import com.template.templateapi.persistence.repository.RoleRepository;
import com.template.templateapi.service.dto.request.role.RoleRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleEntity> getAll() {
        return this.roleRepository.findAll();
    }

    public Page<RoleEntity> getAll(Pageable pageable) {
        return this.roleRepository.findAll(pageable);
    }

    public RoleEntity getRoleById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public RoleEntity getRoleByName(String name){
        return this.roleRepository.findByName(name).orElse(null);
    }

    public RoleEntity saveRole(RoleEntity roleEntity){
        if(this.roleRepository.findByName(roleEntity.getName()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists.");
        }

        return this.roleRepository.save(roleEntity);
    }

    public RoleEntity updateRole(Long id, RoleRequest request){
        RoleEntity role = this.roleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if(RoleNames.SUPER_ADMIN.equals(role.getName()) || RoleNames.SUPER_ADMIN.equals(request.getName())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This role cannot be modified.");
        }

        if (!Objects.equals(role.getName(), request.getName()) && this.roleRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists.");
        }

        role.setName(request.getName());

        return this.roleRepository.save(role);
    }

    public RoleEntity assignPermission(Long id, List<Long> permissionIds){
        RoleEntity role = this.roleRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if(RoleNames.SUPER_ADMIN.equals(role.getName())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This role cannot be modified.");
        }

        List<PermissionEntity> foundPermissions = this.permissionRepository.findAllById(permissionIds);

        if(foundPermissions.size() != permissionIds.size()){
            throw new EntityNotFoundException("One or more permissions not found.");
        }

        role.setPermissions(new HashSet<>(foundPermissions));

        return this.roleRepository.save(role);
    }

    public void deleteRole(Long id){
        if(!this.roleRepository.existsById(id)){
            throw new EntityNotFoundException("Role not found.");
        }

        this.roleRepository.deleteById(id);
    }
}
