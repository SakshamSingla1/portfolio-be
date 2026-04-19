package com.portfolio.repositories;

import com.portfolio.entities.Profile;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByPhone(String phone);

    Optional<Profile> findByUserName(String userName);

    boolean existsByEmail(String newEmail);

    Page<Profile> findAll(Pageable pageable);

    Page<Profile> findByStatus(StatusEnum status, Pageable pageable);

    @Query("""
    {
      $or: [
        { "fullName": { $regex: ?0, $options: "i" } },
        { "email": { $regex: ?0, $options: "i" } },
        { "userName": { $regex: ?0, $options: "i" } }
      ]
    }
    """)
    Page<Profile> search(String search, Pageable pageable);

    @Query("""
    {
      $and: [
        {
          $or: [
            { "fullName": { $regex: ?0, $options: "i" } },
            { "email": { $regex: ?0, $options: "i" } },
            { "userName": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<Profile> searchByStatus(
            String search,
            StatusEnum status,
            Pageable pageable
    );

    @Query("""
    {
      $and: [
        {
          $or: [
            { "fullName": { $regex: ?0, $options: "i" } },
            { "email": { $regex: ?0, $options: "i" } },
            { "userName": { $regex: ?0, $options: "i" } }
          ]
        },
        { "roleId": ?1 }
      ]
    }
    """)
    Page<Profile> searchByRole(
            String search,
            String role,
            Pageable pageable
    );

    @Query("""
    {
      $and: [
        {
          $or: [
            { "fullName": { $regex: ?0, $options: "i" } },
            { "email": { $regex: ?0, $options: "i" } },
            { "userName": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 },
        { "roleId": ?2 }
      ]
    }
    """)
    Page<Profile> searchByRoleAndStatus(
            String search,
            StatusEnum status,
            String role,
            Pageable pageable
    );
}
