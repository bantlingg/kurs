package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.EquipmentLicense;
import ru.kurs.entity.Worker;
import ru.kurs.entity.Equipment;
import ru.kurs.repository.EquipmentLicenseRepository;
import ru.kurs.repository.WorkerRepository;
import ru.kurs.repository.EquipmentRepository;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/equipment-licenses")
@RequiredArgsConstructor
public class EquipmentLicenseController {

    private final EquipmentLicenseRepository licenseRepository;
    private final WorkerRepository workerRepository;
    private final EquipmentRepository equipmentRepository;

    @GetMapping
    public String listLicenses(Model model) {
        log.info("Accessing equipment licenses list page");
        List<EquipmentLicense> licenses = licenseRepository.findAll();
        log.info("Found {} licenses", licenses.size());
        model.addAttribute("licenses", licenses);
        return "equipment-licenses";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/new")
    public String newLicenseForm(Model model) {
        log.info("Accessing new equipment license form");
        model.addAttribute("license", new EquipmentLicense());
        List<Worker> workers = workerRepository.findAll();
        List<Equipment> equipment = equipmentRepository.findAll();
        model.addAttribute("workers", workers);
        model.addAttribute("equipment", equipment);
        return "equipment-license-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping
    public String createLicense(@ModelAttribute EquipmentLicense license) {
        log.info("Creating new equipment license: {}", license);
        try {
            licenseRepository.save(license);
            log.info("Successfully created license with id: {}", license.getId());
            return "redirect:/equipment-licenses";
        } catch (Exception e) {
            log.error("Error creating license", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editLicenseForm(@PathVariable Long id, Model model) {
        log.info("Accessing edit equipment license form for id: {}", id);
        try {
            EquipmentLicense license = licenseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
            log.info("Found license: {}", license);
            
            List<Worker> workers = workerRepository.findAll();
            log.info("Found {} workers", workers.size());
            
            List<Equipment> equipment = equipmentRepository.findAll();
            log.info("Found {} equipment items", equipment.size());
            
            model.addAttribute("license", license);
            model.addAttribute("workers", workers);
            model.addAttribute("equipment", equipment);
            
            log.info("Successfully prepared model for edit form");
            return "equipment-license-form";
        } catch (Exception e) {
            log.error("Error accessing edit form for license id: " + id, e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateLicense(@PathVariable Long id, @ModelAttribute EquipmentLicense license) {
        log.info("Updating equipment license with id: {}", id);
        EquipmentLicense existingLicense = licenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
        
        license.setId(id);
        licenseRepository.save(license);
        log.info("Successfully updated license with id: {}", id);
        return "redirect:/equipment-licenses";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteLicense(@PathVariable Long id) {
        log.info("Deleting equipment license with id: {}", id);
        licenseRepository.deleteById(id);
        return "redirect:/equipment-licenses";
    }
} 