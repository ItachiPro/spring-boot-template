package com.template.templateapi.persistence.repository;

import com.template.templateapi.persistence.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);

    @Query("SELECT r FROM RoleEntity r " +
            "WHERE UPPER(r.name) <> UPPER(:hiddenRole)")
    List<RoleEntity> findAllVisibleRoles(@Param("hiddenRole") String hiddenRole);

    @Query("SELECT r FROM RoleEntity r " +
            "WHERE UPPER(r.name) <> UPPER(:hiddenRole)")
    Page<RoleEntity> findAllVisibleRoles(@Param("hiddenRole") String hiddenRole, Pageable pageable);

    Optional<RoleEntity> findByIdAndNameNot(Long id, String hiddenRole);
}
