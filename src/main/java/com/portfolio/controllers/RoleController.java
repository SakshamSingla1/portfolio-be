package com.portfolio.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portfolio.dtos.Role.RoleListResponseDTO;
import com.portfolio.dtos.Role.RolePermissionResponseDTO;
import com.portfolio.dtos.Role.RoleRequestBodyDTO;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Create role", description = "Creates a new role with optional permission mappings. Requires SUPER_ADMIN role.")
    @PostMapping
    public ResponseEntity<ResponseModel<RoleListResponseDTO>> createRole(
            @RequestBody RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException {
        RoleListResponseDTO responseDTO = roleService.upsertRole(null, roleRequestBodyDTO);
        return ApiResponse.respond(responseDTO, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Update role", description = "Updates an existing role and its permission mappings by ID. Requires SUPER_ADMIN role.")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<RoleListResponseDTO>> updateRole(@PathVariable Long id,
            @RequestBody RoleRequestBodyDTO roleRequestBodyDTO) throws GenericException {
        RoleListResponseDTO responseDTO = roleService.upsertRole(id, roleRequestBodyDTO);
        return ApiResponse.respond(responseDTO, ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get role with permissions by ID", description = "Fetches a role along with its full permission set by role ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<RolePermissionResponseDTO>> getRolePermissionsByRoleId(@PathVariable Long id)
            throws GenericException, JsonProcessingException {
        return ApiResponse.respond(roleService.getRolePermissionsByRoleId(id),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get all roles", description = "Returns a paginated list of roles with optional filters for search, IDs, nav links, permissions, status, and date range.")
    @GetMapping
    public ResponseEntity<ResponseModel<Page<RoleListResponseDTO>>> getAllRolesByCriteria(
            @RequestParam(required = false) String search, @RequestParam(required = false) String roleIds,
            @RequestParam(required = false) String navLinkIds, @RequestParam(required = false) String permissionIds,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
            @PageableDefault(size = 10) Pageable pageable) throws GenericException {
        return ApiResponse.respond(
                roleService.getAllRolesByCriteria(search, roleIds, navLinkIds, permissionIds, status, startDate,
                        endDate, pageable),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get role permissions by user ID", description = "Fetches the role and its permissions for the given user ID.")
    @GetMapping("/user/{usedId}")
    public ResponseEntity<ResponseModel<RolePermissionResponseDTO>> getRolePermissionsByUserId(
            @PathVariable Long usedId) throws GenericException, JsonProcessingException {
        return ApiResponse.respond(roleService.getRolePermissionsByRoleId(usedId),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get role by name", description = "Fetches a role record by its exact name.")
    @GetMapping("/name/{name}")
    public ResponseEntity<ResponseModel<RoleListResponseDTO>> getRoleByName(@PathVariable String name)
            throws GenericException {
        return ApiResponse.respond(roleService.getRoleByName(name),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }

    @Operation(summary = "Get role by ID", description = "Fetches a role record by its ID (lightweight, without permission details).")
    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseModel<RoleListResponseDTO>> getRoleById(@PathVariable Long id)
            throws GenericException {
        return ApiResponse.respond(roleService.getRoleById(id),
                ApiResponse.SUCCESS, ApiResponse.FAILED);
    }
}
