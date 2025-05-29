package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.ExplosionLicense;
import ru.kurs.entity.Worker;
import ru.kurs.repository.ExplosionLicenseRepository;
import ru.kurs.repository.WorkerRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/explosion-licenses")
@RequiredArgsConstructor
public class ExplosionLicenseController {

    private final ExplosionLicenseRepository licenseRepository;
    private final WorkerRepository workerRepository;

    @GetMapping
    public String listLicenses(Model model) {
        log.info("Accessing explosion licenses list page");
        List<ExplosionLicense> licenses = licenseRepository.findAll();
        log.info("Found {} licenses", licenses.size());
        model.addAttribute("licenses", licenses);
        return "explosion-licenses";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/new")
    public String newLicenseForm(Model model) {
        log.info("Accessing new explosion license form");
        model.addAttribute("license", new ExplosionLicense());
        List<Worker> workers = workerRepository.findAll();
        model.addAttribute("workers", workers);
        return "explosion-license-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping
    public String createLicense(@ModelAttribute ExplosionLicense license) {
        log.info("Creating new explosion license: {}", license);
        try {
            licenseRepository.save(license);
            log.info("Successfully created license with id: {}", license.getId());
            return "redirect:/explosion-licenses";
        } catch (Exception e) {
            log.error("Error creating license", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editLicenseForm(@PathVariable Long id, Model model) {
        log.info("Accessing edit explosion license form for id: {}", id);
        try {
            ExplosionLicense license = licenseRepository.findById(id)
                    .orElseThrow(() -> {
                        log.error("License not found with id: {}", id);
                        return new RuntimeException("License not found with id: " + id);
                    });
            log.info("Found license: {}", license);
            
            List<Worker> workers = workerRepository.findAll();
            log.info("Found {} workers", workers.size());
            
            model.addAttribute("license", license);
            model.addAttribute("workers", workers);
            
            log.info("Successfully prepared model for edit form");
            return "explosion-license-form";
        } catch (Exception e) {
            log.error("Error accessing edit form for license id: {} - {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateLicense(@PathVariable Long id, @ModelAttribute ExplosionLicense license) {
        log.info("Updating explosion license with id: {}", id);
        try {
            ExplosionLicense existingLicense = licenseRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
            
            license.setId(id);
            licenseRepository.save(license);
            log.info("Successfully updated license with id: {}", id);
            return "redirect:/explosion-licenses";
        } catch (Exception e) {
            log.error("Error updating license with id: " + id, e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteLicense(@PathVariable Long id) {
        log.info("Deleting explosion license with id: {}", id);
        try {
            licenseRepository.deleteById(id);
            log.info("Successfully deleted license with id: {}", id);
            return "redirect:/explosion-licenses";
        } catch (Exception e) {
            log.error("Error deleting license with id: " + id, e);
            throw e;
        }
    }
} 