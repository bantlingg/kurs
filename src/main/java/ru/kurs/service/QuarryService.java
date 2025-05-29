package ru.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kurs.entity.Quarry;
import ru.kurs.repository.QuarryRepository;
import ru.kurs.entity.User;
import ru.kurs.entity.Role;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuarryService {
    private final QuarryRepository quarryRepository;

    public List<Quarry> getAllQuarries() {
        return quarryRepository.findAll();
    }

    public List<Quarry> getQuarriesForUser(User user) {
        if (user.getRole() == Role.ADMIN || user.getRole() == Role.GUEST) {
            return getAllQuarries();
        }
        // For other roles, return only their associated quarry
        return user.getQuarry() != null ? List.of(user.getQuarry()) : List.of();
    }

    public Quarry getQuarryById(Long id) {
        return quarryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quarry Id:" + id));
    }

    @Transactional
    public Quarry createQuarry(Quarry quarry) {
        return quarryRepository.save(quarry);
    }

    @Transactional
    public Quarry updateQuarry(Quarry quarry) {
        Quarry existingQuarry = quarryRepository.findById(quarry.getId())
                .orElseThrow(() -> new IllegalArgumentException("Quarry not found with id: " + quarry.getId()));

        // Обновляем все поля существующего карьера
        existingQuarry.setName(quarry.getName());
        existingQuarry.setLocation(quarry.getLocation());
        existingQuarry.setArea(quarry.getArea());
        existingQuarry.setDepth(quarry.getDepth());
        existingQuarry.setStatus(quarry.getStatus());

        return quarryRepository.save(existingQuarry);
    }

    @Transactional
    public void deleteQuarry(Long id) {
        if (!quarryRepository.existsById(id)) {
            throw new IllegalArgumentException("Quarry not found with id: " + id);
        }
        quarryRepository.deleteById(id);
    }
}