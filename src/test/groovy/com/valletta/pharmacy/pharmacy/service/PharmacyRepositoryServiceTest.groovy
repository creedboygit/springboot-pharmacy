package com.valletta.pharmacy.pharmacy.service

import com.valletta.pharmacy.AbstractIntegrationContainerBaseTest
import com.valletta.pharmacy.pharmacy.entity.Pharmacy
import com.valletta.pharmacy.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired

class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private PharmacyRepositoryService pharmacyRepositoryService

    @Autowired
    private PharmacyRepository pharmacyRepository

    def setup() {
        pharmacyRepository.deleteAll()
    }

    def "PharmacyRepository update - dirty checking success"() {
        given:
        String inputAddress = "서울 특별시 강남구 청담동"
        String modifiedAddress = "서울특별시 강남구 역삼동"
        String name = "강남 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddress(entity.getId(), modifiedAddress)
        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == modifiedAddress
    }

    def "PharmacyRepository update - dirty checking fail"() {
        given:
        String inputAddress = "서울 특별시 강남구 청담동"
        String modifiedAddress = "서울특별시 강남구 역삼동"
        String name = "강남 약국"

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .build()

        when:
        def entity = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddressWithoutTransaction(entity.getId(), modifiedAddress)
        def result = pharmacyRepository.findAll()

        then:
        result.get(0).getPharmacyAddress() == inputAddress
    }

    def "self invocation"() {

        given:
        String address = "서울 특별시 강남구 청담동"
        String name = "강남 약국"
        double latitude = 36.11
        double longtitude = 128.11

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .latitude(latitude)
                .longtitude(longtitude)
                .build()

        when:
        pharmacyRepositoryService.bar(Arrays.asList(pharmacy))

        then:
        def e = thrown(RuntimeException.class)
        def result = pharmacyRepositoryService.findAll()
        result.size() == 1 // 트랜잭션이 적용되지 않는다 (롤백 적용 X)
    }

    def "transactional readOnly test - 읽기 전용일 경우 dirty checking이 반영되지 않는다."() {

        given:
        String inputAddress = "서울특별시 강남구 청담동"
        String modifiedAddress = "서울특별시 서초구 양재동"
        String name = "강남 약국"
        double latitude = 36.11
        double longtitude = 128.11

        def input = Pharmacy.builder()
                .pharmacyAddress(inputAddress)
                .pharmacyName(name)
                .latitude(latitude)
                .longtitude(longtitude)
                .build()

        when:
        def pharmacy = pharmacyRepository.save(input)
        pharmacyRepositoryService.startReadOnlyMethod(pharmacy.id)

        then:
        def result = pharmacyRepositoryService.findAll()
        result.get(0).getPharmacyAddress() == inputAddress
    }
}
