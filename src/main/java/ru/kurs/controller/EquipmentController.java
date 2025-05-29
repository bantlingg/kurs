package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.Equipment;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.User;
import ru.kurs.repository.EquipmentRepository;
import ru.kurs.security.CustomUserDetails;
import ru.kurs.service.QuarryService;

@Controller
@RequestMapping("/equipment")
@RequiredArgsConstructor
@Slf4j
public class EquipmentController {
    private final EquipmentRepository equipmentRepository;
    private final QuarryService quarryService;

    @GetMapping
    public String listEquipment(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing equipment list page for user: {}", userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var equipment = equipmentRepository.findByQuarryIn(quarries);
        model.addAttribute("equipment", equipment);
        return "equipment";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/new")
    public String newEquipmentForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing new equipment form for user: {}", userDetails.getUser().getUserName());
        model.addAttribute("equipment", new Equipment());
        model.addAttribute("quarries", quarryService.getQuarriesForUser(userDetails.getUser()));
        return "equipment-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/new")
    public String createEquipment(@ModelAttribute Equipment equipment) {
        log.info("Creating new equipment: {}", equipment.getName());
        equipmentRepository.save(equipment);
        return "redirect:/equipment";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editEquipmentForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        log.info("Accessing edit form for equipment with id: {} for user: {}", id, userDetails.getUser().getUserName());
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
        model.addAttribute("equipment", equipment);
        model.addAttribute("quarries", quarryService.getQuarriesForUser(userDetails.getUser()));
        return "equipment-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateEquipment(@PathVariable Long id, @ModelAttribute Equipment equipment) {
        log.info("Updating equipment with id: {}", id);
        Equipment existingEquipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));

        equipment.setId(id);
        equipmentRepository.save(equipment);
        log.info("Successfully updated equipment with id: {}", id);
        return "redirect:/equipment";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable Long id) {
        log.info("Deleting equipment with id: {}", id);
        equipmentRepository.deleteById(id);
        return "redirect:/equipment";
    }
}