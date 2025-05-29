package ru.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kurs.entity.ExplosionLicense;
import java.util.List;

public interface ExplosionLicenseRepository extends JpaRepository<ExplosionLicense, Long> {
    List<ExplosionLicense> findByWorkerId(Long workerId);
    List<ExplosionLicense> findByStatus(String status);
    List<ExplosionLicense> findByLicenseType(String licenseType);
} 