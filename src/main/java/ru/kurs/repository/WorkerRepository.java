package ru.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kurs.entity.Worker;
import ru.kurs.entity.Quarry;
import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findByQuarryIn(List<Quarry> quarries);
}