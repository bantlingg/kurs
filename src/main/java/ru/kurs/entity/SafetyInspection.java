package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "safety_inspections")
public class SafetyInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quarry_id", nullable = false)
    private Quarry quarry;

    @Column(nullable = false)
    private LocalDateTime inspectionDate;

    @Column(nullable = false)
    private String inspectionType; // Плановая, Внеплановая, По жалобе

    @Column(nullable = false)
    private String status; // ПРОЙДЕНА, НЕ ПРОЙДЕНА, ТРЕБУЕТСЯ ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА

    @Column(nullable = false, columnDefinition = "TEXT")
    private String findings;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @ManyToOne
    @JoinColumn(name = "inspector_id", nullable = false)
    private Worker inspector;

    @Column(nullable = false)
    private LocalDateTime nextInspectionDate;
} 