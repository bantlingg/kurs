package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime reportDate;

    @Column(nullable = false)
    private String reportType; // Ежедневный, Еженедельный, Ежемесячный

    @ManyToOne
    @JoinColumn(name = "quarry_id", nullable = false)
    private Quarry quarry;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Worker author;
} 