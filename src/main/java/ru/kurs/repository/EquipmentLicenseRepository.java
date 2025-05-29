package ru.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kurs.entity.EquipmentLicense;
import java.util.List;

public interface EquipmentLicenseRepository extends JpaRepository<EquipmentLicense, Long> {
    List<EquipmentLicense> findByWorkerId(Long workerId);
    List<EquipmentLicense> findByEquipmentId(Long equipmentId);
    List<EquipmentLicense> findByStatus(String status);
} 