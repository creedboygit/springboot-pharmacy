package com.valletta.pharmacy.pharmacy.service;

import com.valletta.pharmacy.pharmacy.entity.Pharmacy;
import com.valletta.pharmacy.pharmacy.repository.PharmacyRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRepositoryService {

    private final PharmacyRepository pharmacyRepository;

    // self invocation test
//    @Transactional
    public void bar(List<Pharmacy> pharmacyList) {
        log.info("bar CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        foo(pharmacyList);
    }

    // self invocation test
    public void foo(List<Pharmacy> pharmacyList) {
        log.info("foo CurrentTransactionName: {}", TransactionSynchronizationManager.getCurrentTransactionName());
        pharmacyList.forEach(pharmacy -> {
            pharmacyRepository.save(pharmacy);
            throw new RuntimeException("error"); // 예외 발생
        });
    }

    // read only test
    @Transactional(readOnly = true)
    public void startReadOnlyMethod(Long id) {
        pharmacyRepository.findById(id).ifPresent(pharmacy -> {
            pharmacy.changePharmacyAddress("서울특별시 강남구");
        });
    }

    @Transactional
    public void updateAddress(Long id, String address) {

        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    // for test
    public void updateAddressWithoutTransaction(Long id, String address) {

        Pharmacy entity = pharmacyRepository.findById(id).orElse(null);

        if (Objects.isNull(entity)) {
            log.error("[PharmacyRepositoryService updateAddress] not found id: {}", id);
            return;
        }

        entity.changePharmacyAddress(address);
    }

    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        return pharmacyRepository.findAll();
    }
}
