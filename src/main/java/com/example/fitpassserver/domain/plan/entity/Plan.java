package com.example.fitpassserver.domain.plan.entity;

import com.example.fitpassserver.domain.member.entity.Member;
import com.example.fitpassserver.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "plan")
@SQLRestriction("plan_type <> 'NONE'") //<> 같지않다
public class Plan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;
    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    @Column(name = "sid", nullable = false)
    private String sid;
    @Column(name = "payment_count", nullable = false)
    private int paymentCount;

    public void changePlanType(PlanType planType) {
        this.planType = planType;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void addPaymentCount() {
        this.paymentCount += 1;
    }

    public boolean isRegularPlan() {
        return paymentCount >= 1 && this.planType.equals(PlanType.NONE);
    }

    public void updatePlanDate() {
        this.planDate = LocalDate.now();
    }
}
