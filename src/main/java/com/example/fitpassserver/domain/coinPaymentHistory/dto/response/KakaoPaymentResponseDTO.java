package com.example.fitpassserver.domain.coinPaymentHistory.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record KakaoPaymentResponseDTO(
        @JsonProperty("tid")
        String tid,
        @JsonProperty("next_redirect_mobile_url")
        String nextRedirectMobileUrl,
        @JsonProperty("next_redirect_pc_url")
        String nextRedirectPcUrl,
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
