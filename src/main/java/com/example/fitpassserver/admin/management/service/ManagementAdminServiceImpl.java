package com.example.fitpassserver.admin.management.service;

import com.example.fitpassserver.admin.management.dto.request.ManagementAdminRequestDTO;
import com.example.fitpassserver.admin.management.dto.response.ManagementAdminResponseDTO;
import com.example.fitpassserver.domain.coin.entity.CoinTypeEntity;
import com.example.fitpassserver.domain.coin.exception.CoinErrorCode;
import com.example.fitpassserver.domain.coin.exception.CoinException;
import com.example.fitpassserver.domain.coin.repository.CoinTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagementAdminServiceImpl implements ManagementAdminService {
    private final CoinTypeRepository coinTypeRepository;

    /**
     * 코인 관리
     **/
    @Transactional
    public void updateAllCoins(List<ManagementAdminRequestDTO.UpdateCoinManagementDTO> requestList) {
        for (ManagementAdminRequestDTO.UpdateCoinManagementDTO request : requestList) {
            CoinTypeEntity coinTypeEntity = coinTypeRepository.findByName(request.getName())
                    .orElseThrow(() -> new CoinException(CoinErrorCode.COIN_NOT_FOUND));

            coinTypeEntity.updateCoinType(
                    request.getName(),
                    request.getPrice(),
                    request.getCoinQuantity(),
                    request.getCoinAddition(),
                    request.getExpirationPeriod()
            );
        }
    }

    @Override
    public List<ManagementAdminResponseDTO.CoinInfoDTO> getAllCoins() {
        List<CoinTypeEntity> coins = coinTypeRepository.findAllSortedByCoinQuantity();
        return coins.stream()
                .map(coin -> new ManagementAdminResponseDTO.CoinInfoDTO(coin))
                .collect(Collectors.toList());
    }
}
