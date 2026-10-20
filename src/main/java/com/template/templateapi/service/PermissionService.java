package com.template.templateapi.service;

import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.repository.PermissionRepository;
import com.template.templateapi.service.dto.request.permission.PermissionRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<PermissionEntity> getAll(){
        return this.permissionRepository.findAll();
    }

    public Page<PermissionEntity> getAll(Pageable pageable) {
        return this.permissionRepository.findAll(pageable);
    }

    public PermissionEntity getPermissionById(Long id) {
        return this.permissionRepository.findById(id).orElseThrow(()   -> new EntityNotFoundException("Permission not found."));
    }

    public PermissionEntity getPermissionByName(String name){
        return this.permissionRepository.findByName(name).orElseThrow(()-> new EntityNotFoundException("Permission not found."));
    }

    public PermissionEntity savePermission(PermissionEntity permissionEntity){
        if(this.permissionRepository.findByName(permissionEntity.getName()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Permission already exists");
        }

        return this.permissionRepository.save(permissionEntity);
    }

    public PermissionEntity updatePermission(Long id, PermissionRequest request){
        PermissionEntity permission = this.permissionRepository.findById(id).orElseThrow  (() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));

        if(!Objects.equals(permission.getName(), request.getName()) && this.permissionRepository.findByName(request.getName()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Permission already exists");
        }

        permission.setName(request.getName());

        return this.permissionRepository.save(permission);
    }

    public void deletePermission(Long id){
        if(!this.permissionRepository.existsById(id)){
            throw new EntityNotFoundException("Permission not found");
        }

        this.permissionRepository.deleteById(id);
    }
}
