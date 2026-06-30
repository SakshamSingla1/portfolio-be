package com.portfolio.dao.role;

import com.portfolio.dtos.Role.RoleListResponseDTO;
import com.portfolio.dtos.Role.RoleMappedModule;
import com.portfolio.entities.Role;
import com.portfolio.enums.RoleStatusEnum;
import com.portfolio.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class RoleDao {

    private final RoleRepository roleRepository;

    public RoleDao(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Page<RoleListResponseDTO> findByCriteria(String search, List<Long> navLinkIds, RoleStatusEnum status, Pageable pageable){
        return roleRepository.findByCriteria(search, navLinkIds, status, pageable);
    }

    public List<RoleMappedModule> findDistinctModulesByRoleId(Long roleId) {
        return roleRepository.findDistinctModulesByRoleId(roleId);
    }

    public Map<Long, List<RoleMappedModule>> findDistinctModulesByRoleIds(List<Long> roleIds) {
        if (roleIds.isEmpty()) return Map.of();
        return roleRepository.findDistinctModulesByRoleIds(roleIds).stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        LinkedHashMap::new,
                        Collectors.mapping(
                                row -> new RoleMappedModule((Long) row[1], (String) row[2]),
                                Collectors.toList()
                        )
                ));
    }
}
