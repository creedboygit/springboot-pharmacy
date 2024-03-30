package com.valletta.pharmacy.pharmacy.repository

import com.valletta.pharmacy.AbstractIntegrationContainerBaseTest
import com.valletta.pharmacy.pharmacy.entity.Pharmacy
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRepositoryTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepository pharmacyRepository

    def "PharmacyRepository save"() {

        given:
        String address = "서울특별시 강남구 청담동"
        String name = "강남 약국"
        double latitude = 36.11
        double longtitude = 128.11

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longtitude(longtitude)
                .build()

//        println pharmacy.toString()

        when:
        def result = pharmacyRepository.save(pharmacy)

        then:
        result.getPharmacyAddress() == address
        result.getPharmacyName() == name
        result.getLatitude() == latitude
        result.getLongtitude() == longtitude
    }
}
