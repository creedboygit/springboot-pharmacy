package com.valletta.pharmacy.pharmacy.service;

import com.valletta.pharmacy.api.dto.DocumentDto;
import com.valletta.pharmacy.api.dto.KakaoApiResponseDto;
import com.valletta.pharmacy.api.service.KakaoAddressSearchService;
import com.valletta.pharmacy.direction.dto.OutputDto;
import com.valletta.pharmacy.direction.entity.Direction;
import com.valletta.pharmacy.direction.service.Base62Service;
import com.valletta.pharmacy.direction.service.DirectionService;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;
    private final Base62Service base62Service;

    private static final String COMMA = ",";
    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";

    @Value("${pharmacy.recommendation.base.url}")
    private String baseUrl;

    public List<OutputDto> recommendPharmacyList(String address) {

        KakaoApiResponseDto kakaoApiResponseDto = kakaoAddressSearchService.requestAddressSearch(address);

        if (Objects.isNull(kakaoApiResponseDto) || CollectionUtils.isEmpty(kakaoApiResponseDto.getDocumentList())) {
            log.error("[PharmacyRecommendationService recommendPharmacyList fail] Input address: {}", address);
            return Collections.emptyList();
        }

        DocumentDto documentDto = kakaoApiResponseDto.getDocumentList().get(0);

        List<Direction> directionList = directionService.buildDirectionList(documentDto); // data.go.kr 정보 이용
//        List<Direction> directionList = directionService.buildDirectionListByCategoryApi(documentDto); // 카카오 카테고리 API 이용

        return directionService.saveAll(directionList)
            .stream()
            .map(this::convertToOutputDto)
            .collect(Collectors.toList());
    }

    private OutputDto convertToOutputDto(Direction direction) {

        // roadViewUrl 만들기
        String roadViewUrl = makeRoadViewUrl(direction.getTargetLatitude(), direction.getTargetLongitude());

        return OutputDto.builder()
            .pharmacyAddress(direction.getTargetAddress())
            .pharmacyName(direction.getTargetPharmacyName())
            .directionUrl(baseUrl + base62Service.encodeDirectionId(direction.getId()))
            .roadViewUrl(roadViewUrl)
            .distance(String.format("%.2f km", direction.getDistance()))
            .build();
    }

    private String makeRoadViewUrl(double latitude, double longitude) {
        return ROAD_VIEW_BASE_URL + String.join(COMMA, String.valueOf(latitude), String.valueOf(longitude));
    }
}
