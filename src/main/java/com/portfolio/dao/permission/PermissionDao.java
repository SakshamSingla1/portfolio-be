package com.portfolio.dao.permission;

import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import com.portfolio.repositories.PermissionRepository;
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
public class PermissionDao {

    private final PermissionRepository permissionRepository;

    public PermissionDao(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public List<PermissionResponseDTO> searchByName(String name) {
        return permissionRepository.searchByName(name);
    }

    public Optional<PermissionResponseDTO> findDTOById(Long id) {
        return permissionRepository.findDTOById(id);
    }

    public Page<PermissionResponseDTO> findByCriteria(String search, List<Long> permissionIds, Pageable pageable) {
        return permissionRepository.findByCriteria(search, permissionIds, pageable);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }

    public boolean existsByNameAndIdNot(String name, Long id) {
        return permissionRepository.existsByNameAndIdNot(name, id);
    }

    public Map<Long, Permission> findAllByIdAsMap(List<Long> ids) {
        return permissionRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));
    }

    public void delete(Permission permission) {
        permissionRepository.delete(permission);
    }
}
