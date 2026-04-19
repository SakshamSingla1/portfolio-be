package com.portfolio.repositories;

import com.portfolio.entities.RolePermission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends MongoRepository<RolePermission, String> {

    List<RolePermission> findByRoleId(String roleId);

    List<RolePermission> findByNavLinkId(String navLinkId);

    List<RolePermission> findByPermissionId(String permissionId);

    List<RolePermission> findByRoleIdIn(List<String> roleIds);

    @Query("{ 'roleId': ?0, 'navLinkId': ?1, 'permissionId': ?2 }")
    RolePermission findByRoleIdAndNavLinkIdAndPermissionId(String roleId, String navLinkId, String permissionId);

    void deleteByRoleId(String roleId);

    List<RolePermission> findByRoleIdAndNavLinkId(String roleId, String navLinkId);

    List<RolePermission> findByNavLinkIdIn(List<String> navLinkIds);

    List<RolePermission> findByPermissionIdIn(List<String> permissionIds);
}
