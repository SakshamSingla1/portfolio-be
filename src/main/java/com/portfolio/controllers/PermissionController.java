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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Endpoints for managing permissions")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PermissionController {

        private final PermissionService permissionService;

        @Operation(summary = "Create a new permission")
        @PostMapping
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> createPermission(
                        @RequestBody PermissionRequestDTO request) throws GenericException {
                PermissionResponseDTO response = permissionService.createPermission(request);
                return ApiResponse.respond(
                                response,
                                "Permission created successfully",
                                "Failed to create permission");
        }

        @Operation(summary = "Update an existing permission")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> updatePermission(
                        @PathVariable String id,
                        @RequestBody PermissionRequestDTO request) throws GenericException {
                PermissionResponseDTO response = permissionService.updatePermission(id, request);
                return ApiResponse.respond(
                                response,
                                "Permission updated successfully",
                                "Failed to update permission");
        }

        @Operation(summary = "Delete a permission")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseModel<String>> deletePermission(@PathVariable String id) throws GenericException {
                permissionService.deletePermission(id);
                return ApiResponse.respond(
                                "Permission deleted successfully",
                                "Permission deleted successfully",
                                "Failed to delete permission");
        }

        @Operation(summary = "Get permission by ID")
        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<PermissionResponseDTO>> getPermissionById(@PathVariable String id)
                        throws GenericException {
                PermissionResponseDTO response = permissionService.getPermissionById(id);
                return ApiResponse.respond(
                                response,
                                "Permission fetched successfully",
                                "Failed to fetch permission");
        }

        @Operation(summary = "Get all permissions with pagination")
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

        @Operation(summary = "Get all permissions (legacy endpoint)")
        @GetMapping("/all")
        public ResponseEntity<ResponseModel<List<Permission>>> getModulesWithChildren() {
                return ApiResponse.successResponse(permissionService.getAllPermissions(), ApiResponse.SUCCESS);
        }
}
