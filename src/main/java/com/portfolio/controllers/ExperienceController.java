package com.portfolio.controllers;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    @Autowired
    ExperienceService experienceService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(@RequestBody ExperienceRequest req) throws Exception {
        return ApiResponse.successResponse(experienceService.create(req),ApiResponse.SUCCESS);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<ExperienceResponse>>> getAll() {
        return ApiResponse.successResponse(experienceService.getAll(),ApiResponse.SUCCESS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable Integer id) throws GenericException {
        return ApiResponse.successResponse(experienceService.getById(id),ApiResponse.SUCCESS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(@PathVariable Integer id, @RequestBody ExperienceRequest req) throws Exception {
        return ApiResponse.successResponse(experienceService.update(id, req),ApiResponse.SUCCESS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) throws GenericException {
        return ApiResponse.successResponse(experienceService.delete(id),ApiResponse.SUCCESS);
    }
}
