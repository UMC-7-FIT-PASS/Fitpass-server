package com.example.fitpassserver.domain.fitness.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FitnessRecommendResponse {
    private Long fitnessId;
    private String fitnessName;
    private double distance;
    private String imageUrl;
}