package com.portfolio.controllers;

import com.portfolio.dtos.NavLinks.NavLinkRequestDTO;
import com.portfolio.dtos.NavLinks.NavLinkResponseDTO;
import com.portfolio.dtos.NavLinks.GroupedNavLinkResponseDTO;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.NavLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/navlinks")
public class NavLinkController {

        @Autowired
        private NavLinkService navLinkService;

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PostMapping
        public ResponseEntity<ResponseModel<NavLinkResponseDTO>> createNavLink(
                        @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws GenericException {

                NavLinkResponseDTO responseDTO = navLinkService.createNavLink(navLinkRequestDTO);

                return ApiResponse.respond(
                                responseDTO,
                                "Nav Link created successfully",
                                "Failed to create nav link");
        }

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @PutMapping("/{id}")
        public ResponseEntity<ResponseModel<NavLinkResponseDTO>> updateNavLink(
                        @PathVariable String id,
                        @RequestBody NavLinkRequestDTO navLinkRequestDTO) throws GenericException {

                NavLinkResponseDTO responseDTO = navLinkService.updateNavLink(id, navLinkRequestDTO);

                return ApiResponse.respond(
                                responseDTO,
                                "Nav Link updated successfully",
                                "Failed to update nav link");
        }

        @PreAuthorize("hasRole('SUPER_ADMIN')")
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseModel<String>> deleteNavLink(
                        @PathVariable String id) throws GenericException {

                navLinkService.deleteNavLink(id);

                return ApiResponse.respond(
                                "OK",
                                "Nav Link deleted successfully",
                                "Failed to delete nav link");
        }

        @GetMapping
        public ResponseEntity<ResponseModel<List<NavLinkResponseDTO>>> getNavLinks() {

                List<NavLinkResponseDTO> responseDTO = navLinkService.getNavLinks();

                return ApiResponse.respond(
                                responseDTO,
                                "Nav Links fetched successfully",
                                "Failed to fetch nav links");
        }

        @GetMapping("/all")
        public ResponseEntity<ResponseModel<Page<NavLinkResponseDTO>>> getAllNavLinks(
                        Pageable pageable,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false, defaultValue = "updatedAt") String sortBy,
                        @RequestParam(required = false, defaultValue = "desc") String sortDir,
                        @RequestParam(required = false) StatusEnum status) {

                Page<NavLinkResponseDTO> responseDTO = navLinkService.getAllNavLinks(
                                pageable,
                                search,
                                status,
                                sortBy,
                                sortDir);

                return ApiResponse.respond(
                                responseDTO,
                                "Nav Links fetched successfully",
                                "Failed to fetch nav links");
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseModel<NavLinkResponseDTO>> getNavLink(
                        @PathVariable String id) throws GenericException {

                NavLinkResponseDTO responseDTO = navLinkService.getNavLink(id);

                return ApiResponse.respond(
                                responseDTO,
                                "Nav Link fetched successfully",
                                "Failed to fetch nav link");
        }

        @GetMapping("/grouped")
        public ResponseEntity<ResponseModel<List<GroupedNavLinkResponseDTO>>> getGroupedNavLinks() {

                List<GroupedNavLinkResponseDTO> responseDTO = navLinkService.getGroupedNavLinks();

                return ApiResponse.respond(
                                responseDTO,
                                "Grouped Nav Links fetched successfully",
                                "Failed to fetch grouped nav links");
        }
}
