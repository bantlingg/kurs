package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "explosions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Explosion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double power;

    @Column(nullable = false)
    private String status; // Завершен, В процессе, Планируется

    @ManyToOne
    @JoinColumn(name = "quarry_id", nullable = false)
    private Quarry quarry;
} 