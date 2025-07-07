package com.portfolio.controllers;

import com.portfolio.dtos.EducationRequest;
import com.portfolio.dtos.EducationResponse;
import com.portfolio.enums.DegreeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.EducationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/education")
public class EducationController {

    @Autowired
    EducationService educationService;

    @PostMapping
    public ResponseEntity<ResponseModel<EducationResponse>> createEducation(@RequestBody EducationRequest educationRequest) throws GenericException {
        return educationService.createEducation(educationRequest);
    }

    @PutMapping("/{degree}")
    public ResponseEntity<ResponseModel<EducationResponse>> updateEducation(@PathVariable DegreeEnum degree, @RequestBody EducationRequest educationRequest) throws GenericException {
        return educationService.updateEducation(degree, educationRequest);
    }

    @GetMapping("/{degree}")
    public ResponseEntity<ResponseModel<EducationResponse>> getEducation(@PathVariable DegreeEnum degree) throws GenericException {
        return educationService.findByDegree(degree);
    }

    @GetMapping
    public ResponseEntity<ResponseModel<List<EducationResponse>>> getEducations() throws GenericException {
        return educationService.findAllEducations();
    }
}
