package com.example.fitpassserver.domain.coin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "coin_type_admin")
public class CoinTypeEntity {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "coin_type", nullable = false, unique = true)
    private CoinType coinType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "coin_quantity", nullable = false) //지급 코인 => count
    private int coinQuantity;

    @Column(name = "coin_addition", nullable = false) //추가 코인
    private int coinAddition;

    @Column(name = "expiration_period", nullable = false) //유효기간 => deadLine
    private int expirationPeriod;


    public void updateCoinType(String name, int price, int coinQuantity, int coinAddition, int expirationPeriod) {
        this.name = name;
        this.price = price;
        this.coinQuantity = coinQuantity;
        this.coinAddition = coinAddition;
        this.expirationPeriod = expirationPeriod;
    }
}
