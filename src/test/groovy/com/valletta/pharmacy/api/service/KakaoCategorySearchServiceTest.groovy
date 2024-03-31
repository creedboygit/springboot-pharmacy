package com.valletta.pharmacy.api.service

import com.valletta.pharmacy.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoCategorySearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    KakaoCategorySearchService kakaoCategorySearchService

    def "파라미터 값이 valid하다면, requestPharmacyCategorySearch 메소드는 정상적으로 document를 반환한다."() {

        given:
        double latitude = 37.549993550833;
        double longitude = 127.00774201005;
        double radius = 10;

        when:
        def result = kakaoCategorySearchService.requestPharmacyCategorySearch(latitude, longitude, radius)

        println result.metaDto.toString()
        println result.documentList.toString()

        then:
        result.documentList.size() > 0
        result.metaDto.totalCount > 0
        result.documentList.getFirst().addressName != null
    }
}
