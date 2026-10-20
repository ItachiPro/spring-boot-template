package com.template.templateapi.service;

import com.template.templateapi.common.RoleNames;
import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.repository.PermissionRepository;
import com.template.templateapi.persistence.repository.RoleRepository;
import com.template.templateapi.service.dto.request.role.RoleRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleEntity> getAll() {
        return this.roleRepository.findAllVisibleRoles(RoleNames.SUPER_ADMIN);
    }

    public Page<RoleEntity> getAll(Pageable pageable) {
        return this.roleRepository.findAllVisibleRoles(RoleNames.SUPER_ADMIN, pageable);
    }

    public RoleEntity getRoleById(Long id) {
        return this.roleRepository.findByIdAndNameNot(id, RoleNames.SUPER_ADMIN).orElseThrow(() -> new EntityNotFoundException("Role not found."));
    }

    public RoleEntity getRoleByName(String name){
        return this.roleRepository.findByName(name).orElseThrow(()  -> new EntityNotFoundException("Role not found."));
    }

    public RoleEntity saveRole(RoleEntity roleEntity){
        assertNotReserved(roleEntity.getName());

        if(this.roleRepository.findByName(roleEntity.getName()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists.");
        }

        return this.roleRepository.save(roleEntity);
    }

    public RoleEntity updateRole(Long id, RoleRequest request){
        assertNotReserved(request.getName());

        RoleEntity role = findEditableRole(id);

        if (!Objects.equals(role.getName(), request.getName()) && this.roleRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role already exists.");
        }

        role.setName(request.getName());

        return this.roleRepository.save(role);
    }

    public RoleEntity assignPermission(Long id, List<Long> permissionIds){
        RoleEntity role = findEditableRole(id);

        Set<Long> uniqueIds = new HashSet<>(permissionIds);
        List<PermissionEntity> foundPermissions = this.permissionRepository.findAllById(uniqueIds);

        if(foundPermissions.size() != uniqueIds.size()){
            throw new EntityNotFoundException("One or more permissions not found.");
        }

        role.setPermissions(new HashSet<>(foundPermissions));

        return this.roleRepository.save(role);
    }

    public void deleteRole(Long id){
        RoleEntity role = findEditableRole(id);

        this.roleRepository.delete(role);
    }

    private RoleEntity findEditableRole(Long id){
        RoleEntity role = this.roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        if(RoleNames.SUPER_ADMIN.equalsIgnoreCase(role.getName())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This role cannot be modified.");
        }

        return role;
    }

    private void assertNotReserved(String name){
        if(RoleNames.SUPER_ADMIN.equalsIgnoreCase(name)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This role name is reserved.");
        }
    }
}
