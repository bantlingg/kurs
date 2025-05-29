package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "explosion_licenses")
public class ExplosionLicense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String licenseType; // Взрывник, Помощник взрывника, Наблюдатель

    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, REVOKED

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Integer maxPower; // Максимальная мощность взрыва в кг
} 