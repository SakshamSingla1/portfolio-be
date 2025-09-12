package com.portfolio.controllers;

import com.portfolio.dtos.Skill.SkillDropdown;
import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.dtos.logo.LogoDropdown;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skill")
public class SkillController {

    @Autowired
    SkillService skillService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<SkillResponse>> create(@RequestBody SkillRequest req) throws GenericException {
        return ApiResponse.successResponse(skillService.create(req),ApiResponse.SUCCESS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(@PathVariable("id") Integer id,@RequestBody SkillRequest req) throws GenericException {
        return ApiResponse.successResponse(skillService.update(id,req),ApiResponse.SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> findById(@PathVariable("id") Integer id) throws GenericException {
        return ApiResponse.successResponse(skillService.getById(id),ApiResponse.SUCCESS);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<SkillResponse>>> getAll() {
        return ApiResponse.successResponse(skillService.getAll(),ApiResponse.SUCCESS);
    }

    @GetMapping("/dropdown")
    public ResponseEntity<ResponseModel<Page<SkillDropdown>>> findSkill(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable
    ) throws GenericException {
        return ApiResponse.respond(
                skillService.getAllSkillsByPage(pageable,search),
                ApiResponse.SUCCESS, ApiResponse.FAILED
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) throws GenericException {
        return ApiResponse.successResponse(skillService.delete(id),ApiResponse.SUCCESS);
    }
}
