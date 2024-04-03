package com.valletta.pharmacy.direction.controller;

import com.valletta.pharmacy.direction.entity.Direction;
import com.valletta.pharmacy.direction.service.DirectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Controller
public class DirectionController {

    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";
    private static final String COMMA = ",";

    private final DirectionService directionService;

    @GetMapping("/dir/{encodedId}")
    public String searchDirection(@PathVariable("encodedId") String encodedId) {

        Direction resultDirection = directionService.findById(encodedId);

        String params = String.join(COMMA, resultDirection.getTargetPharmacyName(), String.valueOf(resultDirection.getTargetLatitude()), String.valueOf(resultDirection.getTargetLongitude()));
        String directUrl = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();

        log.info("direction params: {}, url: {}", params, directUrl);

        return "redirect:" + directUrl;
    }
}
