package com.valletta.pharmacy.direction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {

    private String pharmacyName; // 약국명
    private String pharmacyAddress; // 약국 주소
    private String directionUrl; // 길안내 url
    private String roadViewUrl; // 로드뷰 url
    private String distance; // 고객 주소와 약국 주소 사이의 거리
}