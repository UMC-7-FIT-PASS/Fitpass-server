package com.example.fitpassserver.domain.plan.dto.event;

import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.domain.plan.dto.response.PlanSubscriptionResponseDTO;
import com.example.fitpassserver.domain.plan.dto.response.SubscriptionResponseDTO;
import com.example.fitpassserver.domain.plan.entity.Plan;
import com.example.fitpassserver.domain.plan.entity.PlanType;

public record PlanSuccessEvent() {
    public record PlanChangeAllSuccessEvent(
            String phoneNumber,
            String planName
    ) {
    }

    public record PlanApproveSuccessEvent(
            Member member,
            PlanSubscriptionResponseDTO dto

    ) {
    }

    public record PlanPaymentAllSuccessEvent(
            String planName,
            String phoneNumber,
            int totalAmount,
            String paymentMethod
    ) {
    }

    public record PlanChangeSuccessEvent(
            Plan plan,
            PlanType planType

    ) {
    }

    public record RegularSubscriptionApprovedEvent(
            Plan plan,
            SubscriptionResponseDTO dto
    ) {
    }

}

