package com.portfolio.repositories;

import com.portfolio.entities.NavLink;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface NavLinkRepository extends MongoRepository<NavLink, String> {

    boolean existsByRolesContainingAndIndex(String role, String index);

    boolean existsByRolesContainingAndPath(String role, String path);

    Optional<NavLink> findByRolesContainingAndIndex(String role, String index);

    Optional<NavLink> findByRolesContainingAndPath(String role, String path);

    List<NavLink> findByRolesContaining(String role);

    Page<NavLink> findAll(Pageable pageable);

    Page<NavLink> findByStatus(StatusEnum status, Pageable pageable);

    Page<NavLink> findByRolesContaining(String role, Pageable pageable);

    Page<NavLink> findByStatusAndRolesContaining(
            StatusEnum status,
            String role,
            Pageable pageable
    );

    @Query("""
    {
      $or: [
        { "name": { $regex: ?0, $options: "i" } },
        { "path": { $regex: ?0, $options: "i" } }
      ]
    }
    """)
    Page<NavLink> search(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<NavLink> searchByStatus(
            String search,
            StatusEnum status,
            Pageable pageable
    );

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } }
          ]
        },
        { "roles": ?1 }
      ]
    }
    """)
    Page<NavLink> searchByRole(
            String search,
            String role,
            Pageable pageable
    );

    @Query("""
    {
      $and: [
        {
          $or: [
            { "name": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 },
        { "roles": ?2 }
      ]
    }
    """)
    Page<NavLink> searchByRoleAndStatus(
            String search,
            StatusEnum status,
            String role,
            Pageable pageable
    );
}
