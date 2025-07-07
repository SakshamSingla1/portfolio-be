package com.portfolio.controllers;

import com.portfolio.dtos.ProfileRequest;
import com.portfolio.dtos.ProfileResponse;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    ProfileService profileService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<ProfileResponse>> create(@RequestBody ProfileRequest req) throws GenericException {
        return profileService.create(req);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<ProfileResponse>> get() throws GenericException {
        return profileService.get();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<ProfileResponse>> update(@PathVariable Integer id, @RequestBody ProfileRequest req) throws GenericException {
        return profileService.update(id, req);
    }
}
