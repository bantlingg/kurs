package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "quarry_id")
    private Quarry quarry;
} 