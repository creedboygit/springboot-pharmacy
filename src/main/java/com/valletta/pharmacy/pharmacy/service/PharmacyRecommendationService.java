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
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRecommendationService {

    private final KakaoAddressSearchService kakaoAddressSearchService;
    private final DirectionService directionService;

    private static final String COMMA = ",";
    private static final String ROAD_VIEW_BASE_URL = "https://map.kakao.com/link/roadview/";
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";


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

        // directUrl 만들기
        String params = makeParams(direction.getTargetPharmacyName(), direction.getTargetLatitude(), direction.getTargetLongitude());
        String directUrl = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();

        // roadViewUrl 만들기
        String roadViewUrl = makeRoadViewUrl(direction.getTargetLatitude(), direction.getTargetLongitude());

        log.info("direction params: {}, url: {}", params, directUrl);

        return OutputDto.builder()
            .pharmacyAddress(direction.getTargetAddress())
            .pharmacyName(direction.getTargetPharmacyName())
            .directionUrl(directUrl)
//            .roadViewUrl(ROAD_VIEW_BASE_URL + direction.getTargetLatitude() + "," + direction.getTargetLongitude())
            .roadViewUrl(roadViewUrl)
            .distance(String.format("%.2f km", direction.getDistance()))
            .build();
    }

    private static String makeParams(String name, double latitude, double longitude) {
        return String.join(COMMA,
            name,
            String.valueOf(latitude),
            String.valueOf(longitude));
    }

    private String makeRoadViewUrl(double latitude, double longitude) {
        return ROAD_VIEW_BASE_URL + String.join(COMMA, String.valueOf(latitude), String.valueOf(longitude));
    }
}
