package com.template.templateapi.persistence.repository;

import com.template.templateapi.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "LEFT JOIN FETCH u.permissions " +
            "WHERE u.username = :username")
    Optional<UserEntity> findByUsernameWithRolesAndPermission(String username);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id <> :currentUserId " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    List<UserEntity> findAllVisible(Long currentUserId, String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id <> :currentUserId " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Page<UserEntity> findVisibleUsers(Long currentUserId, String hiddenRole, Pageable pageable);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id = :id " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleById(Long id, String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.username = :username " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleByUsername(String username, String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.email = :email " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleByEmail(String username, String hiddenRole);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
