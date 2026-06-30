package com.portfolio.dao.testimonial;

import com.portfolio.dtos.Testimonial.TestimonialResponseDTO;
import com.portfolio.entities.Testimonial;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.TestimonialRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TestimonialDao {

    private final TestimonialRepository testimonialRepository;

    public TestimonialDao(TestimonialRepository testimonialRepository) {
        this.testimonialRepository = testimonialRepository;
    }

    public Testimonial save(Testimonial testimonial) {
        return testimonialRepository.save(testimonial);
    }

    public Optional<Testimonial> findById(Long id) {
        return testimonialRepository.findById(id);
    }

    public void deleteById(Long id) {
        testimonialRepository.deleteById(id);
    }

    public Optional<TestimonialResponseDTO> findDTOById(Long id) {
        return testimonialRepository.findDTOById(id);
    }

    public Page<TestimonialResponseDTO> findByCriteria(Long profileId, String search, Pageable pageable) {
        return testimonialRepository.findByCriteria(profileId, search, pageable);
    }


    public boolean existsByProfileIdAndOrder(Long profileId, String order) {
        return testimonialRepository.existsByProfileIdAndOrder(profileId, order);
    }

    public boolean existsByProfileIdAndOrderAndIdNot(Long profileId, String order, Long id) {
        return testimonialRepository.existsByProfileIdAndOrderAndIdNot(profileId, order, id);
    }

    public List<Testimonial> findByProfileIdAndStatusOrderByOrderAsc(Long profileId, StatusEnum status) {
        return testimonialRepository.findByProfileIdAndStatusOrderByOrderAsc(profileId, status);
    }

    public long countByProfileId(Long profileId) {
        return testimonialRepository.countByProfileId(profileId);
    }

    public Optional<Testimonial> findTop1ByProfileIdOrderByUpdatedAtDesc(Long profileId) {
        return testimonialRepository.findTop1ByProfileIdOrderByUpdatedAtDesc(profileId);
    }

    public boolean existsById(Long id) {
        return testimonialRepository.existsById(id);
    }

}
