package com.valletta.pharmacy.pharmacy.service;

import com.valletta.pharmacy.api.dto.DocumentDto;
import com.valletta.pharmacy.api.dto.KakaoApiResponseDto;
import com.valletta.pharmacy.api.service.KakaoAddressSearchService;
import com.valletta.pharmacy.direction.dto.OutputDto;
import com.valletta.pharmacy.direction.entity.Direction;
import com.valletta.pharmacy.direction.service.DirectionService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    public List<OutputDto> recommendPharmacyList(String address) {

        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);

//        List<Direction> directionList = directionService.buildDirectionList(documentDto); // data.go.kr 정보 이용
        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto); // 카카오 카테고리 API 이용

        return directionService.saveAll(directionList)
            .stream()
            .map(this::convertToOutputDto)
            .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {

        return OutputDto.builder()
            .pharmacyAddress(direction.getTargetAddress())
            .pharmacyName(direction.getTargetPharmacyName())
            .directionUrl("todo") // TODO
            .roadViewUrl("todo") // TODO
            .distance(String.format("%.2f km", direction.getDistance()))
            .build();
    }
}
