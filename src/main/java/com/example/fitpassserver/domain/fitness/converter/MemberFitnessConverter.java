package com.example.fitpassserver.domain.fitness.converter;

import com.example.fitpassserver.domain.fitness.dto.MemberFitnessRequestDTO;
import com.example.fitpassserver.domain.fitness.dto.MemberFitnessResponseDTO;
import com.example.fitpassserver.domain.fitness.dto.response.MemberFitnessResDTO;
import com.example.fitpassserver.domain.fitness.entity.Fitness;
import com.example.fitpassserver.domain.fitness.entity.MemberFitness;
import com.example.fitpassserver.domain.fitness.entity.Status;
import com.example.fitpassserver.domain.fitness.util.DistanceCalculator;
import com.example.fitpassserver.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class MemberFitnessConverter {


    public static MemberFitness toEntity(Member member, Fitness fitness, MemberFitnessRequestDTO.CreateMemberFitnessRequestDTO dto) {
        return MemberFitness.builder()
                .status(Status.NONE)
                .activeTime(null)
                .isAgree(dto.isAgree())
                .member(member)
                .fitness(fitness)
                .build();
    }

    public static MemberFitnessResDTO.MemberFitnessPreviewDTO toDto(MemberFitness memberFitness, String imageUrl) {
        double distance = DistanceCalculator.distance(
                memberFitness.getMember().getLatitude(), memberFitness.getMember().getLongitude(),
                memberFitness.getFitness().getLatitude(), memberFitness.getFitness().getLongitude()
        );

        return MemberFitnessResDTO.MemberFitnessPreviewDTO.builder()
                .id(memberFitness.getId())
                .status(memberFitness.getStatus())
                .activeTime(memberFitness.getActiveTime())
                .memberId(memberFitness.getMember().getId())
                .fitnessId(memberFitness.getFitness().getId())
                .fitnessName(memberFitness.getFitness().getName())
                .address(memberFitness.getFitness().getAddress())
                .distance(distance)
                .imageUrl(imageUrl)
                .build();
    }



    public static MemberFitnessResponseDTO.CreateMemberFitnessResponseDTO toCreateMemberFitnessResponseDTO(MemberFitness memberFitness) {
        return MemberFitnessResponseDTO.CreateMemberFitnessResponseDTO.builder()
                .memberFitnessId(memberFitness.getId())
                .build();
    }

    public static MemberFitnessResponseDTO.CancelMemberFitnessResponseDTO toCancelMemberFitnessResponseDTO(MemberFitness memberFitness) {
        return MemberFitnessResponseDTO.CancelMemberFitnessResponseDTO.builder()
                .memberFitnessId(memberFitness.getId())
                .build();
    }

    public static MemberFitnessResDTO.NoneProgressGroupDTO toGroupDto1(
            List<MemberFitness> memberFitnessList, Map<Long, String> imageUrlMap) {

        List<MemberFitnessResDTO.MemberFitnessPreviewDTO> none = new ArrayList<>();
        List<MemberFitnessResDTO.MemberFitnessPreviewDTO> progress = new ArrayList<>();

        for (MemberFitness memberFitness : memberFitnessList) {
            Long fitnessId = memberFitness.getFitness().getId();
            String imageUrl = imageUrlMap.getOrDefault(fitnessId, null);
            MemberFitnessResDTO.MemberFitnessPreviewDTO dto = toDto(memberFitness, imageUrl);

            switch (memberFitness.getStatus()) {
                case NONE -> none.add(dto);
                case PROGRESS -> progress.add(dto);
            }
        }

        return MemberFitnessResDTO.NoneProgressGroupDTO.builder()
                .none(none)
                .progress(progress)
                .build();
    }

    public static MemberFitnessResDTO.DoneReviewedGroupDTO toGroupDto2(
            List<MemberFitness> memberFitnessList, Map<Long, String> imageUrlMap) {

        Map<Status, List<MemberFitnessResDTO.MemberFitnessPreviewDTO>> groupedDtos = memberFitnessList.stream()
                .map(memberFitness -> MemberFitnessConverter.toDto(memberFitness, imageUrlMap.getOrDefault(memberFitness.getFitness().getId(), null)))
                .collect(Collectors.groupingBy(
                        MemberFitnessResDTO.MemberFitnessPreviewDTO::getStatus,
                        Collectors.toList()
                ));

        return MemberFitnessResDTO.DoneReviewedGroupDTO.builder()
                .done(groupedDtos.getOrDefault(Status.DONE, List.of()))
                .reviewed(groupedDtos.getOrDefault(Status.REVIEWED, List.of()))
                .build();
    }

    public static MemberFitnessResDTO.PagedDoneReviewedGroupDTO toPagedGroupDto(
            List<MemberFitness> memberFitnessList, Map<Long, String> imageUrlMap, boolean hasNext) {
        MemberFitnessResDTO.DoneReviewedGroupDTO groupDTO = toGroupDto2(memberFitnessList, imageUrlMap);

        // 다음 커서 계산
        Long nextCursor = memberFitnessList.isEmpty() ? null
                : memberFitnessList.get(memberFitnessList.size() - 1).getId();

        return MemberFitnessResDTO.PagedDoneReviewedGroupDTO.builder()
                .group(groupDTO)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }

}