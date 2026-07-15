package com.portfolio.controllers;

import com.portfolio.dtos.Permission.PermissionRequestDTO;
import com.portfolio.dtos.Permission.PermissionResponseDTO;
import com.portfolio.entities.Permission;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Endpoints for managing permissions")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

        private final PermissionService permissionService;

        @Operation(summary = "Create a new permission", description = "Creates a new permission entry. Requires SUPER_ADMIN role.")
        @PostMapping
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> createPermission(
                        @Valid @RequestBody PermissionRequestDTO request) throws GenericException {
                PermissionResponseDTO response = permissionService.createPermission(request);
                return ApiResponse.respond(
                                response,
                                "Permission created successfully",
                                "Failed to create permission");
        }

        @Operation(summary = "Update an existing permission", description = "Updates an existing permission by ID. Requires SUPER_ADMIN role.")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> updatePermission(
                        @PathVariable Long id,
                        @Valid @RequestBody PermissionRequestDTO request) throws GenericException {
                PermissionResponseDTO response = permissionService.updatePermission(id, request);
                return ApiResponse.respond(
                                response,
                                "Permission updated successfully",
                                "Failed to update permission");
        }

        @Operation(summary = "Delete a permission", description = "Deletes a permission by ID. Requires SUPER_ADMIN role.")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseModel<String>> deletePermission(@PathVariable Long id) throws GenericException {
                permissionService.deletePermission(id);
                return ApiResponse.respond(
                                "Permission deleted successfully",
                                "Permission deleted successfully",
                                "Failed to delete permission");
        }

        @Operation(summary = "Get permission by ID", description = "Fetches a single permission record by its ID.")
        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> getPermissionById(@PathVariable Long id)
                        throws GenericException {
                PermissionResponseDTO response = permissionService.getPermissionById(id);
                return ApiResponse.respond(
                                response,
                                "Permission fetched successfully",
                                "Failed to fetch permission");
        }

        @Operation(summary = "Get all permissions with pagination", description = "Returns a paginated list of permissions with optional search and ID filter.")
        @GetMapping
        public ResponseEntity<ResponseModel<Page<PermissionResponseDTO>>> getAllPermissions(
                        Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String permissionIds) throws GenericException {
                Page<PermissionResponseDTO> response = permissionService.getAllPermissionsPaginated(pageable, search,
                                permissionIds);
                return ApiResponse.respond(
                                response,
                                "Permissions fetched successfully",
                                "Failed to fetch permissions");
        }

        @Operation(summary = "Get all permissions (legacy endpoint)", description = "Returns the full list of all permissions without pagination. Legacy endpoint.")
        @GetMapping("/all")
        public ResponseEntity<ResponseModel<List<Permission>>> getModulesWithChildren() {
                return ApiResponse.successResponse(permissionService.getAllPermissions(), ApiResponse.SUCCESS);
        }
}
