package com.template.templateapi.service;

import com.template.templateapi.persistence.entity.PermissionEntity;
import com.template.templateapi.persistence.entity.RoleEntity;
import com.template.templateapi.persistence.entity.UserEntity;
import com.template.templateapi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserSecurityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = this.userRepository.findByUsernameWithRolesAndPermission(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(this.grantedAuthorities(userEntity))
                .build();
    }

    private Set<GrantedAuthority> grantedAuthorities(UserEntity userEntity) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for(RoleEntity role : userEntity.getRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            for(PermissionEntity permission : role.getPermissions()){
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }

        for (PermissionEntity permission : userEntity.getPermissions()){
            authorities.add(new SimpleGrantedAuthority(permission.getName()));
        }

        return authorities;
    }
}
