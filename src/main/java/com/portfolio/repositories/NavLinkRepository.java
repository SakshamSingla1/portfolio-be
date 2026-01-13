package com.portfolio.repositories;

import com.portfolio.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NavLinkRepository extends MongoRepository<NavLink, String> {

    boolean existsByIndex(String index);
    boolean existsByPath(String path);

    Optional<NavLink> findByIndex(String index);
    Optional<NavLink> findByPath(String path);

    Page<NavLink> findAll(Pageable pageable);

    Page<NavLink> findByStatus(StatusEnum status, Pageable pageable);

    @Query("""
    {
      $or: [
        { "label": { $regex: ?0, $options: "i" } },
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
            { "label": { $regex: ?0, $options: "i" } },
            { "path": { $regex: ?0, $options: "i" } }
          ]
        },
        { "status": ?1 }
      ]
    }
    """)
    Page<NavLink> searchByStatus(String search, StatusEnum status, Pageable pageable);
}
