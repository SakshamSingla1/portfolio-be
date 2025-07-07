package com.portfolio.repositories;


import com.portfolio.entities.Education;
import com.portfolio.enums.DegreeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EducationRespository extends JpaRepository<Education, Integer> {
    Education findByDegree(DegreeEnum degree);
    List<Education> findAll();
}
