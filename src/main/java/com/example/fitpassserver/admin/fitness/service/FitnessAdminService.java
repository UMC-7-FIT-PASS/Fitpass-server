package com.example.fitpassserver.admin.fitness.service;

import com.example.fitpassserver.admin.fitness.dto.request.FitnessAdminRequestDTO;
import com.example.fitpassserver.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FitnessAdminService {
    Long createFitness(Member member, MultipartFile mainImage, List<MultipartFile> additionalImages, FitnessAdminRequestDTO.CreateFitnessDTO dto) throws IOException;

}
