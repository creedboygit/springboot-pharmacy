package com.valletta.pharmacy.direction.service;

import com.valletta.pharmacy.api.dto.DocumentDto;
import com.valletta.pharmacy.api.service.KakaoCategorySearchService;
import com.valletta.pharmacy.direction.entity.Direction;
import com.valletta.pharmacy.direction.repository.DirectionRepository;
import com.valletta.pharmacy.pharmacy.service.PharmacySearchService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectionService {

    private static final int MAX_SEARCH_COUNT = 3; // 약국 최대 검색 갯수
    private static final double RADIUS_KM = 10.0; // 반경 10km 이내
    private static final double METER_TO_KILOMETER = 0.001; // 미터를 킬로미터로 변환할 때 곱해줄 숫자
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";
    private static final String COMMA = ",";

    private final DirectionRepository directionRepository;
    private final PharmacySearchService pharmacySearchService;
    private final KakaoCategorySearchService kakaoCategorySearchService;
    private final Base62Service base62Service;

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) {
            return Collections.emptyList();
        }
        return directionRepository.saveAll(directionList);
    }

    public String findDirectionUrlById(String encodedId) {
        Long decodedId = base62Service.decodeDirectionId(encodedId);
        Direction direction = directionRepository.findById(decodedId).orElse(null);

        String params = String.join(COMMA, direction.getTargetPharmacyName(), String.valueOf(direction.getTargetLatitude()), String.valueOf(direction.getTargetLongitude()));
        return UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();
    }

    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) {
            return null;
        }

        // 약국 데이터 조회
        return pharmacySearchService.searchPharmacyDtoList()
            .stream()
            .map(pharmacyDto ->
                Direction.builder()
                    .inputAddress(documentDto.getAddressName())
                    .inputLatitude(documentDto.getLatitude())
                    .inputLongitude(documentDto.getLongitude())
                    .targetPharmacyName(pharmacyDto.getPharmacyName())
                    .targetAddress(pharmacyDto.getPharmacyAddress())
                    .targetLatitude(pharmacyDto.getLatitude())
                    .targetLongitude(pharmacyDto.getLongitude())
                    .distance(
                        calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                            pharmacyDto.getLatitude(), pharmacyDto.getLongitude())
                    )
                    .build())
            .filter(direction -> direction.getDistance() <= RADIUS_KM)
            .sorted(Comparator.comparing(Direction::getDistance))
            .limit(MAX_SEARCH_COUNT)
            .collect(Collectors.toList());
    }

    // pharmacy search by category kakao api
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto inputDocumentDto) {

        if (Objects.isNull(inputDocumentDto)) {
            return Collections.emptyList();
        }

        return kakaoCategorySearchService
            .requestPharmacyCategorySearch(inputDocumentDto.getLatitude(), inputDocumentDto.getLongitude(), RADIUS_KM)
            .getDocumentList()
            .stream().map(resultDocumentDto ->
                Direction.builder()
                    .inputAddress(inputDocumentDto.getAddressName())
                    .inputLatitude(inputDocumentDto.getLatitude())
                    .inputLongitude(inputDocumentDto.getLongitude())
                    .targetPharmacyName(resultDocumentDto.getPlaceName())
                    .targetLatitude(resultDocumentDto.getLatitude())
                    .targetLongitude(resultDocumentDto.getLongitude())
                    .distance(resultDocumentDto.getDistance() * METER_TO_KILOMETER) // km 단위
                    .build())
//            .filter(direction -> direction.getDistance() <= RADIUS_KM * METER_TO_KILOMETER)
//            .sorted(Comparator.comparing(Direction::getDistance))
            .limit(MAX_SEARCH_COUNT)
            .collect(Collectors.toList());
    }

    // Haversine formula (두 위도, 경도 사이의 거리를 구하는 공식)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; // Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
