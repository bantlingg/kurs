package ru.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kurs.entity.Quarry;

public interface QuarryRepository extends JpaRepository<Quarry, Long> {
} 