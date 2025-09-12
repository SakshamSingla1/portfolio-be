package com.portfolio.repositories;


import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRespository extends JpaRepository<Education, Integer> {
    Education findByDegree(DegreeEnum degree);
    List<Education> findAll();
}
