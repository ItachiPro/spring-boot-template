package com.template.templateapi.web.config;

import com.template.templateapi.common.RoleNames;
import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.persistence.repository.PermissionRepository;
import com.template.templateapi.persistence.repository.RoleRepository;
import com.template.templateapi.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private static final Map<String, List<String>> MODULES = new LinkedHashMap<>() {{
        put("USER", List.of("LIST", "SHOW", "CREATE", "UPDATE", "DELETE", "ASSIGN_ROLE", "ASSIGN_PERMISSION"));
        put("ROLE", List.of("LIST", "SHOW", "CREATE", "UPDATE", "DELETE", "ASSIGN_PERMISSION"));
        put("PERMISSION", List.of("LIST", "SHOW", "CREATE", "UPDATE", "DELETE"));
    }};

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username}")
    private String superAdminUsername;

    @Value("${app.seed.admin-email}")
    private String superAdminEmail;

    @Value("${app.seed.admin-password}")
    private String superAdminPassword;

    @Override
    public void run(String... args) throws Exception {

        List<PermissionEntity> permissions = seedPermissions();

        getOrCreateRole(RoleNames.SUPER_ADMIN);

        RoleEntity admin = getOrCreateRole(RoleNames.ADMIN);
        RoleEntity manager =  getOrCreateRole(RoleNames.MANAGER);
        RoleEntity user = getOrCreateRole(RoleNames.USER);

        syncPermissions(admin, new HashSet<>(permissions));

        Set<PermissionEntity> managerPermissions = permissions.stream()
                .filter(p -> !p.getName().startsWith("DELETE_")
                        && !p.getName().equals("ASSIGN_PERMISSION_ROLE"))
                .collect(Collectors.toSet());

        syncPermissions(manager, managerPermissions);

        Set<PermissionEntity> userPermissions = permissions.stream()
                .filter(p -> p.getName().startsWith("LIST_") || p.getName().startsWith("SHOW_"))
                .collect(Collectors.toSet());

        syncPermissions(user, userPermissions);

        seedSuperAdmin();
    }

    private List<PermissionEntity> seedPermissions(){
        List<PermissionEntity> permissions = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : MODULES.entrySet()){
            String module = entry.getKey();
            for (String action : entry.getValue()) {
                String name = action + "_" + module;
                permissions.add(getOrCreatePermission(name));
            }
        }

        return permissions;
    }

    private PermissionEntity getOrCreatePermission(String name){
        return this.permissionRepository.findByName(name).orElseGet(() -> {
            PermissionEntity permission = new PermissionEntity();
            permission.setName(name);

            return this.permissionRepository.save(permission);
        });
    }

    private RoleEntity getOrCreateRole(String name){
        return this.roleRepository.findByName(name).orElseGet(() -> {
            RoleEntity role = new RoleEntity();
            role.setName(name);

            return this.roleRepository.save(role);
        });
    }

    private void syncPermissions(RoleEntity role, Set<PermissionEntity> permissions){
        role.setPermissions(permissions);
        this.roleRepository.save(role);
    }

    private void seedSuperAdmin(){
        if(this.userRepository.existsByUsername(this.superAdminUsername) || this.userRepository.existsByEmail(this.superAdminEmail)){
            return;
        }

        RoleEntity superAdminRole = getOrCreateRole(RoleNames.SUPER_ADMIN);

        UserEntity user = new UserEntity();
        user.setUsername(this.superAdminUsername);
        user.setEmail(this.superAdminEmail);
        user.setPassword(this.passwordEncoder.encode(this.superAdminPassword));
        user.setRoles(Set.of(superAdminRole));

        this.userRepository.save(user);
    }
}
