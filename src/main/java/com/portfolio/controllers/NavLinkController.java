package com.portfolio.controllers;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.enums.RoleEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.NavLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/navlinks")
public class NavLinkController {

    @Autowired
    private NavLinkService navLinkService;

    @PostMapping
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> createNavLink(
            @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws GenericException {

        NavLinkResponseDTO responseDTO = navLinkService.createNavLink(navLinkRequestDTO);
        return ApiResponse.respond(responseDTO, "Nav Link created successfully", "Failed to create nav link");
    }

    @PutMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> updateNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index,
            @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws GenericException {

        NavLinkResponseDTO responseDTO = navLinkService.updateNavLink(index, role, navLinkRequestDTO);
        return ApiResponse.respond(responseDTO, "Nav Link updated successfully", "Failed to update nav link");
    }

    @DeleteMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<String>> deleteNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index) throws GenericException {

        navLinkService.deleteNavLink(index, role);

        return ApiResponse.respond(
                "OK",
                "Nav Link deleted successfully",
                "Failed to delete nav link"
        );
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ResponseModel<List<NavLinkResponseDTO>>> getNavLinks(@PathVariable RoleEnum role) {
        List<NavLinkResponseDTO> responseDTO = navLinkService.getNavLinks(role);
        return ApiResponse.respond(responseDTO, "Nav Links fetched successfully", "Failed to fetch nav links");
    }

    @GetMapping
    public ResponseEntity<ResponseModel<Page<NavLinkResponseDTO>>> getAllNavLinks(
            Pageable pageable,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StatusEnum status
    ) {
        Page<NavLinkResponseDTO> responseDTO = navLinkService.getAllNavLinks(pageable, role, search, status, sortBy, sortDir);
        return ApiResponse.respond(responseDTO, "Nav Links fetched successfully", "Failed to fetch nav links");
    }

    @GetMapping("/{role}/{index}")
    public ResponseEntity<ResponseModel<NavLinkResponseDTO>> getNavLink(
            @PathVariable RoleEnum role,
            @PathVariable String index) throws GenericException {

        NavLinkResponseDTO responseDTO = navLinkService.getNavLink(role, index);
        return ApiResponse.respond(responseDTO, "Nav Link fetched successfully", "Failed to fetch nav link");
    }
}
