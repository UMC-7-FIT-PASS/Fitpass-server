package com.example.fitpassserver.domain.fitness.dto.response;

import com.example.fitpassserver.domain.fitness.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


public class MemberFitnessResDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberFitnessPreviewDTO {
        @Schema(description = "회원 피트니스 ID", example = "1")
        private Long id;

        @Schema(description = "패스 상태 (NONE, PROGRESS, DONE)", example = "PROGRESS")
        private Status status;

        @Schema(description = "패스 사용 시 활성화된 시간", example = "2025-01-14T12:00:00")
        private LocalDateTime activeTime;

        @Schema(description = "개인정보 동의 여부", example = "true")
        private boolean isAgree;

        @Schema(description = "회원 ID", example = "2")
        private Long memberId;

        @Schema(description = "피트니스 ID", example = "3")
        private Long fitnessId;

        @Schema(description = "피트니스 이름", example = "시설 이름")
        private String fitnessName;

        @Schema(description = "피트니스 주소", example = "시설 주소")
        private String address;

        @Schema(description = "거리", example = "3.0")
        private double distance;

        private String imageUrl;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoneProgressGroupDTO {
        @Schema(description = "NONE 상태의 패스 리스트")
        private List<MemberFitnessPreviewDTO> none;

        @Schema(description = "PROGRESS 상태의 패스 리스트")
        private List<MemberFitnessPreviewDTO> progress;
    }


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoneReviewedGroupDTO {
        @Schema(description = "DONE 상태의 패스 리스트")
        private List<MemberFitnessResDTO.MemberFitnessPreviewDTO> done;
        @Schema(description = "REVIEWED 상태의 패스 리스트")
        private List<MemberFitnessResDTO.MemberFitnessPreviewDTO> reviewed;
    }

    @Getter
    @Builder
    public static class PagedDoneReviewedGroupDTO{
        public DoneReviewedGroupDTO group;
        public boolean hasNext;
        public Long nextCursor;
    }
}
