package com.example.fitpassserver.domain.coinPaymentHistory.controller;

import com.example.fitpassserver.domain.coinPaymentHistory.dto.request.CoinSinglePayRequestDTO;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.response.KakaoPaymentApproveDTO;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.response.KakaoPaymentResponseDTO;
import com.example.fitpassserver.domain.coinPaymentHistory.service.CoinPaymentHistoryService;
import com.example.fitpassserver.domain.coinPaymentHistory.service.KakaoSinglePaymentService;
import com.example.fitpassserver.domain.member.annotation.CurrentMember;
import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/coin/singlePay")
@RequiredArgsConstructor
@Tag(name = "코인 결제 API", description = "코인 결제 API입니다.")
public class KakaoPaymentController {
    private final KakaoSinglePaymentService paymentService;
    private final CoinPaymentHistoryService coinPaymentHistoryService;

    @Operation(summary = "코인 단건 결제 요청", description = "코인 코인 단건 결제를 요청합니다.")
    @PostMapping()
    public ApiResponse<KakaoPaymentResponseDTO> requestSinglePay(@CurrentMember Member member,
                                                                 @Valid CoinSinglePayRequestDTO body) {
        KakaoPaymentResponseDTO response = paymentService.ready(body);
        coinPaymentHistoryService.createNewCoinPayment(member, response.tid(), body.methodName());
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "코인 단건 결제 성공", description = "결제 성공시 실행되는 API")
    @PostMapping("/success")
    public ApiResponse<KakaoPaymentApproveDTO> approveSinglePay(@CurrentMember Member member,
                                                                @RequestParam("pg_token") String pgToken) {
        return ApiResponse.onSuccess(paymentService.approve(pgToken, coinPaymentHistoryService.getCurrentTid(member)));
    }
}
