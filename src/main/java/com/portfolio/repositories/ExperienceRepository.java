
package com.portfolio.repositories;

import com.portfolio.entities.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Date;

public interface ExperienceRepository extends JpaRepository<Experience, Integer> {
    boolean existsByCompanyNameAndJobTitleAndStartDate(String companyName, String jobTitle, Date startDate);
}
