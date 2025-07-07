package com.portfolio.controllers;

import com.portfolio.dtos.ExperienceRequest;
import com.portfolio.dtos.ExperienceResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    ExperienceService experienceService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<ExperienceResponse>> create(@RequestBody ExperienceRequest req) throws Exception {
        return experienceService.create(req);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<ExperienceResponse>>> getAll() {
        return experienceService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> getById(@PathVariable Integer id) throws GenericException {
        return experienceService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ExperienceResponse>> update(@PathVariable Integer id, @RequestBody ExperienceRequest req) throws Exception {
        return experienceService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) {
        return experienceService.delete(id);
    }
}
