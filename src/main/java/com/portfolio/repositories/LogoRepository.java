package com.portfolio.repositories;

import com.portfolio.entities.Logo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoRepository extends MongoRepository<Logo, String> {

    boolean existsByName(String name);
    
    @Query("""
    {
      $or: [
        { "name": { "$regex": ?0, "$options": "i" } }
      ]
    }
    """)
    Page<Logo> findByNameWithSearch(String search, Pageable pageable);
}
