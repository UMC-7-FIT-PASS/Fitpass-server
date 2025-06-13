package com.example.fitpassserver.domain.coinPaymentHistory.controller;

import com.example.fitpassserver.domain.coinPaymentHistory.dto.request.CompletePaymentRequest;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.request.PaymentDTO;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.request.StartPaymentRequest;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.response.CoinPaymentHistoryResponseListDTO;
import com.example.fitpassserver.domain.coinPaymentHistory.dto.response.PaymentIdResponse;
import com.example.fitpassserver.domain.coinPaymentHistory.exception.PortOneErrorCode;
import com.example.fitpassserver.domain.coinPaymentHistory.exception.PortOneException;
import com.example.fitpassserver.domain.coinPaymentHistory.service.CoinPaymentHistoryService;
import com.example.fitpassserver.domain.coinPaymentHistory.service.command.PGPaymentCommandService;
import com.example.fitpassserver.domain.member.annotation.CurrentMember;
import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.global.apiPayload.ApiResponse;
import io.portone.sdk.server.webhook.Webhook;
import io.portone.sdk.server.webhook.WebhookTransaction;
import io.portone.sdk.server.webhook.WebhookVerifier;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import kotlin.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coin/pay")
@RequiredArgsConstructor
@Tag(name = "코인 결제 API", description = "코인 결제 API입니다.")
public class CoinPaymentController {
    private final CoinPaymentHistoryService coinPaymentHistoryService;
    private final PGPaymentCommandService pgPaymentCommandService;

    private final WebhookVerifier portoneWebhook;

    @Operation(summary = "코인 결제 내역 조회 API", description = "결제 내역 조회 API")
    @Parameters({
            @Parameter(name = "query", description = "전체: ALL(default), 요금제: PLAN, 코인: COIN")
    })
    @GetMapping("/history")
    public ApiResponse<CoinPaymentHistoryResponseListDTO> getCoinHistory(@CurrentMember Member member,
                                                                         @RequestParam(required = false, defaultValue = "ALL") String query,
                                                                         @RequestParam(required = false, defaultValue = "0") Long cursor,
                                                                         @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.onSuccess(coinPaymentHistoryService.getCoinHistory(member, query, cursor, size));
    }

    @Operation(summary = "포트원 결제 시작 요청입니다. PaymentId를 반환합니다.")
    @PostMapping("/start")
    public ApiResponse<?> startPayment(
            @CurrentMember Member member,
            @RequestBody StartPaymentRequest request
    ) {
        PaymentIdResponse response = pgPaymentCommandService.createPaymentId(member, request);
        return ApiResponse.onSuccess(response);
    }

    @Operation(summary = "결제 정보를 실시간으로 전달받기 위한 웹훅입니다.")
    @PostMapping("/payment/webhook")
    public Mono<Unit> handleWebhook(
            @RequestBody String body,
            @RequestHeader("webhook-id") String webhookId,
            @RequestHeader("webhook-timestamp") String webhookTimestamp,
            @RequestHeader("webhook-signature") String webhookSignature
    ) throws PortOneException {
        Webhook webhook;
        try {
            webhook = portoneWebhook.verify(body, webhookId, webhookSignature, webhookTimestamp);
        } catch (Exception e) {
            throw new PortOneException(PortOneErrorCode.PORT_ONE_ERROR_CODE);
        }
        if (webhook instanceof WebhookTransaction transaction) {
            return pgPaymentCommandService.syncPayment(transaction.getData().getPaymentId(), null).map(payment -> Unit.INSTANCE);
        }
        return Mono.empty();
    }

    @Operation(summary = "포트원 결제를 완료합니다. 결제 상태를 검증하고 동기화합니다.")
    @PostMapping("/payment/complete")
    public ApiResponse<PaymentDTO> completePayment(
            @CurrentMember Member member,
            @RequestBody CompletePaymentRequest completePaymentRequest
    ) {
        PaymentDTO dto = pgPaymentCommandService.syncPayment(completePaymentRequest.paymentId(), member).block();
        return ApiResponse.onSuccess(dto);
    }
}
