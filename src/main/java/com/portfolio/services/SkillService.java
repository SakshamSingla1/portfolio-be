// com.portfolio.services.SkillService.java
package com.portfolio.services;

import com.portfolio.dtos.SkillRequest;
import com.portfolio.dtos.SkillResponse;
import com.portfolio.entities.Skill;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.payload.ApiResponse;
import com.portfolio.payload.ResponseModel;
import com.portfolio.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkillService {

    @Autowired
    SkillRepository skillRepository;

    public ResponseEntity<ResponseModel<SkillResponse>> create(SkillRequest request) throws GenericException {
        Skill existingSkill = skillRepository.findByName(request.getName());

        if (existingSkill != null) {
            return ApiResponse.failureResponse(null,"Skill already exists");
        }

        Skill skill = Skill.builder()
                .name(request.getName())
                .level(request.getLevel())
                .category(request.getCategory())
                .build();

        Skill saved = skillRepository.save(skill);
        return ApiResponse.successResponse(mapToResponse(saved), "Skill created successfully");
    }


    public ResponseEntity<ResponseModel<SkillResponse>> update(Integer id, SkillRequest request) throws GenericException {
        Skill existingSkill = skillRepository.findById(id).get();
        if(existingSkill==null){
            return ApiResponse.failureResponse(null,"Skill not found");
        }
        Skill duplicateSkill = skillRepository.findByName(request.getName());
        if (duplicateSkill != null && !duplicateSkill.getId().equals(id)) {
            throw new GenericException(ExceptionCodeEnum.DUPLICATE_SKILL, "Another skill with the same name already exists");
        }
        existingSkill.setName(request.getName());
        existingSkill.setLevel(request.getLevel());
        existingSkill.setCategory(request.getCategory());
        Skill saved = skillRepository.save(existingSkill);
        return ApiResponse.successResponse(mapToResponse(saved), "Skill updated successfully");
    }

    public ResponseEntity<ResponseModel<List<SkillResponse>>> getAll() {
        List<Skill> list = skillRepository.findAll();
        List<SkillResponse> response = list.stream().map(this::mapToResponse).collect(Collectors.toList());
        return ApiResponse.successResponse(response, "Skills fetched successfully");
    }

    public ResponseEntity<ResponseModel<String>> delete(Integer id) {
        skillRepository.deleteById(id);
        return ApiResponse.successResponse("Skill deleted", "Deleted");
    }

    public ResponseEntity<ResponseModel<SkillResponse>> getById(Integer id) throws GenericException {
        Skill existingSkill = skillRepository.findById(id).orElse(null);
        if (existingSkill == null) {
           return ApiResponse.failureResponse(null,"Skill not found");
        }
        return ApiResponse.successResponse(mapToResponse(existingSkill), "Skill found successfully");
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .level(skill.getLevel())
                .category(skill.getCategory())
                .build();
    }
}
