package com.template.templateapi.persistence.repository;

import com.template.templateapi.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "WHERE u.id = :id")
    Optional<UserEntity> findByIdWithRolesAndPermission(@Param("id") Long id);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id <> :currentUserId " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    List<UserEntity> findAllVisible(@Param("currentUserId") Long currentUserId, @Param("hiddenRole") String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id <> :currentUserId " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Page<UserEntity> findVisibleUsers(@Param("currentUserId") Long currentUserId, @Param("hiddenRole") String hiddenRole, Pageable pageable);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.id = :id " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleById(Long id, @Param("hiddenRole") String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.username = :username " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleByUsername(@Param("username") String username, @Param("hiddenRole") String hiddenRole);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.email = :email " +
            "AND NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = :hiddenRole)")
    Optional<UserEntity> findVisibleByEmail(@Param("email") String email, @Param("hiddenRole") String hiddenRole);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
