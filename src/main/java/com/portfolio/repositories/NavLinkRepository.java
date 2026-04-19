package com.portfolio.repositories;

import com.portfolio.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NavLinkRepository extends MongoRepository<NavLink, String> {

    Page<NavLink> findAll(Pageable pageable);

    Page<NavLink> findByStatus(StatusEnum status, Pageable pageable);

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
}
