package ru.kurs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "quarries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quarry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double area;

    @Column(nullable = false)
    private Double depth;

    @Column(nullable = false)
    private String status;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Equipment> equipment;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Worker> workers;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Explosion> explosions;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @ToString.Exclude
    @OneToMany(mappedBy = "quarry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SafetyInspection> safetyInspections;
} 