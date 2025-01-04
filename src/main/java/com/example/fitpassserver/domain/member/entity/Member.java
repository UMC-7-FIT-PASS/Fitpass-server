package com.example.fitpassserver.domain.member.entity;

import com.example.fitpassserver.domain.CoinPaymentHistory.entity.CoinPaymentHistory;
import com.example.fitpassserver.domain.coin.entity.Coin;
import com.example.fitpassserver.domain.fitness.entity.MemberFitness;
import com.example.fitpassserver.domain.plan.entity.Plan;
import com.example.fitpassserver.global.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "provider", nullable = false)
    private String provider;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Column(name = "is_agree", nullable = false)
    private boolean isAgree;

    @Column(name = "is_terms_agreed", nullable = false)
    private boolean isTermsAgreed;

    @Column(name = "is_location_agreed", nullable = false)
    private boolean isLocationAgreed;

    @Column(name = "is_third_party_agreed", nullable = false)
    private boolean isThirdPartyAgreed;

    @Column(name = "is_marketing_agreed",nullable = false)
    private Boolean isMarketingAgreed;

    @Column(name = "profile_image")
    private String profileImage;

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Coin> CoinList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<CoinPaymentHistory> CoinPaymentHistoryList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberFitness> MemberFitnessList = new ArrayList<>();

}