package com.portfolio.repositories;

import com.portfolio.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {

    Optional<Permission> findById(String id);

    List<Permission> findAll();

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Permission> searchByName(String name);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Page<Permission> searchByName(String name, Pageable pageable);

    Page<Permission> findAll(Pageable pageable);

    Page<Permission> findByIdIn(List<String> permissionIds, Pageable pageable);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, String id);

    @Query("{ $and: [ { 'name': { $regex: ?0, $options: 'i' } }, { '_id': { $in: ?1 } } ] }")
    Page<Permission> searchByNameAndIds(String name, List<String> permissionIds, Pageable pageable);

    @Query("{ 'status': ?0 }")
    Page<Permission> findByStatus(String status, Pageable pageable);

    @Query("{ $and: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'status': ?1 } ] }")
    Page<Permission> searchByNameAndStatus(String name, String status, Pageable pageable);
}
