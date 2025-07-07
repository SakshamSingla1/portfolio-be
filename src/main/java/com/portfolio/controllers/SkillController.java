package com.portfolio.controllers;

import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skill")
public class SkillController {

    @Autowired
    SkillService skillService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<SkillResponse>> create(@RequestBody SkillRequest req) throws GenericException {
        return skillService.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> update(@PathVariable("id") Integer id,@RequestBody SkillRequest req) throws GenericException {
        return skillService.update(id,req);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SkillResponse>> findById(@PathVariable("id") Integer id) throws GenericException {
        return skillService.getById(id);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<SkillResponse>>> getAll() {
        return skillService.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> delete(@PathVariable Integer id) {
        return skillService.delete(id);
    }
}
