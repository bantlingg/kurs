package ru.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kurs.entity.Explosion;
import ru.kurs.entity.Quarry;
import java.util.List;

public interface ExplosionRepository extends JpaRepository<Explosion, Long> {
    List<Explosion> findByQuarryId(Long quarryId);

    List<Explosion> findByStatus(String status);

    List<Explosion> findByQuarryIn(List<Quarry> quarries);
}