package com.example.fitpassserver.domain.fitness.service;

import com.example.fitpassserver.domain.fitness.controller.response.FitnessRecommendResponse;
import com.example.fitpassserver.domain.fitness.entity.Fitness;
import com.example.fitpassserver.domain.fitness.repository.FitnessRepository;
import com.example.fitpassserver.domain.fitness.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FitnessRecommendService {
    private final FitnessRepository fitnessRepository;
    private final FitnessImageService fitnessImageService;


    public FitnessRecommendService(FitnessRepository fitnessRepository, FitnessImageService fitnessImageService) {
        this.fitnessRepository = fitnessRepository;
        this.fitnessImageService = fitnessImageService;
    }

    public List<Fitness> getRecommendFitness() {
        return fitnessRepository.findByIsRecommendTrue();
    }

    public List<FitnessRecommendResponse> getRecommendFitnessAsResponse(double userLatitude, double userLongitude) {
        List<Fitness> fitnessList = getRecommendFitness();
        return fitnessList.stream()
                .map(fitness -> {
                    double distance = DistanceCalculator.distance(
                            userLatitude, userLongitude,
                            fitness.getLatitude(), fitness.getLongitude()
                    );
                    String imageUrl = fitnessImageService.getFitnessImage(fitness.getId());

                    return FitnessRecommendResponse.builder()
                            .fitnessId(fitness.getId())
                            .fitnessName(fitness.getName())
                            .distance(distance)
                            .imageUrl(imageUrl)
                            .build();
                })
                .sorted(Comparator.comparingDouble(FitnessRecommendResponse::getDistance))
                // 상위 7개만 반환
                .limit(7)
                .collect(Collectors.toList());
    }
}