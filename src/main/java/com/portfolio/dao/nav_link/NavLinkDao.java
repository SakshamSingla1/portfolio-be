package com.portfolio.dao.nav_link;

import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.entities.NavLink;
import com.portfolio.enums.StatusEnum;
import com.portfolio.repositories.NavLinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class NavLinkDao {

    private final NavLinkRepository navLinkRepository;

    public NavLinkDao(NavLinkRepository navLinkRepository) {
        this.navLinkRepository = navLinkRepository;
    }

    public NavLink save(NavLink navLink) {
        return navLinkRepository.save(navLink);
    }

    public Optional<NavLink> findById(Long id) {
        return navLinkRepository.findById(id);
    }

    public void deleteById(Long id) {
        navLinkRepository.deleteById(id);
    }

    public Page<NavLinkResponseDTO> findByCriteria(String search, StatusEnum status, Pageable pageable) {
        return navLinkRepository.findByCriteria(search, status, pageable);
    }

    public List<NavLink> findAll() {
        return navLinkRepository.findAll();
    }

    public Map<Long, NavLink> findAllByIdAsMap(List<Long> ids) {
        return navLinkRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(NavLink::getId, n -> n));
    }

    public Optional<NavLinkResponseDTO> findDTOById(Long id) {
        return navLinkRepository.findDTOById(id);
    }
}
