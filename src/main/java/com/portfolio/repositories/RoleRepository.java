package com.portfolio.repositories;

import com.portfolio.entities.Role;
import com.portfolio.enums.RoleStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    Page<Role> findByStatus(RoleStatusEnum status, Pageable pageable);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Page<Role> searchByName(String name, Pageable pageable);

    @Query("{ 'status': { $ne: 'DELETED' } }")
    Page<Role> findAllActive(Pageable pageable);

    @Query("{ $and: [ { 'status': { $ne: 'DELETED' } }, { 'name': { $regex: ?0, $options: 'i' } } ] }")
    Page<Role> searchActiveByName(String name, Pageable pageable);

    @Query("{ $and: [ { 'status': { $ne: 'DELETED' } }, { 'status': ?0 } ] }")
    Page<Role> findByStatus(String status, Pageable pageable);

    @Query("{ $and: [ { 'status': { $ne: 'DELETED' } }, { 'name': { $regex: ?0, $options: 'i' } }, { 'status': ?1 } ] }")
    Page<Role> searchByNameAndStatus(String name, String status, Pageable pageable);

    @Query("{ $and: [ { 'status': { $ne: 'DELETED' } }, { '_id': { $in: ?0 } } ] }")
    Page<Role> findByIdIn(List<String> roleIds, Pageable pageable);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    Page<Role> findByName(String name, Pageable pageable);
}
