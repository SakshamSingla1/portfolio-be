package com.portfolio.controllers;

import com.portfolio.dtos.ContactUsRequest;
import com.portfolio.dtos.ContactUsResponse;
import com.portfolio.entities.ContactUs;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ResponseModel;
import com.portfolio.services.ContactUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact-us")
public class ContactUsController {

    @Autowired
    ContactUsService contactUsService;

    @PostMapping("")
    public ResponseEntity<ResponseModel<ContactUsResponse>> save(@RequestBody ContactUsRequest contactUsRequest) {
        return contactUsService.create(contactUsRequest);
    }

    @GetMapping("")
    public ResponseEntity<ResponseModel<List<ContactUsResponse>>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone
    ) throws GenericException {
        if (email != null) return contactUsService.getByEmail(email);
        if (name != null) return contactUsService.getByName(name);
        if (phone != null) return contactUsService.getByPhone(phone);
        return contactUsService.getAllContactUs(); // no params, return all
    }
}
