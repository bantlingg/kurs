package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.SafetyInspection;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.Worker;
import ru.kurs.repository.SafetyInspectionRepository;
import ru.kurs.repository.WorkerRepository;
import ru.kurs.security.CustomUserDetails;
import ru.kurs.service.QuarryService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/safety-inspections")
@RequiredArgsConstructor
public class SafetyInspectionController {

    private final SafetyInspectionRepository inspectionRepository;
    private final WorkerRepository workerRepository;
    private final QuarryService quarryService;

    @GetMapping
    public String listInspections(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing safety inspections list page for user: {}", userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var inspections = inspectionRepository.findByQuarryIn(quarries);
        log.info("Found {} inspections for user", inspections.size());
        model.addAttribute("inspections", inspections);
        return "safety-inspections";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/new")
    public String newInspectionForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing new safety inspection form for user: {}", userDetails.getUser().getUserName());
        model.addAttribute("inspection", new SafetyInspection());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var inspectors = workerRepository.findByQuarryIn(quarries);
        log.info("Found {} quarries and {} inspectors for user", quarries.size(), inspectors.size());
        model.addAttribute("quarries", quarries);
        model.addAttribute("inspectors", inspectors);
        return "safety-inspection-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/new")
    public String createInspection(@ModelAttribute SafetyInspection inspection) {
        log.info("Creating new safety inspection: {}", inspection);
        try {
            inspection.setInspectionDate(LocalDateTime.now());
            inspectionRepository.save(inspection);
            log.info("Successfully created inspection with id: {}", inspection.getId());
            return "redirect:/safety-inspections";
        } catch (Exception e) {
            log.error("Error creating inspection", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editInspectionForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        log.info("Accessing edit form for inspection with id: {} for user: {}", id,
                userDetails.getUser().getUserName());
        SafetyInspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found with id: " + id));
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var inspectors = workerRepository.findByQuarryIn(quarries);
        model.addAttribute("inspection", inspection);
        model.addAttribute("quarries", quarries);
        model.addAttribute("inspectors", inspectors);
        return "safety-inspection-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateInspection(@PathVariable Long id, @ModelAttribute SafetyInspection inspection) {
        log.info("Updating inspection with id: {}", id);
        SafetyInspection existingInspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found with id: " + id));

        existingInspection.setQuarry(inspection.getQuarry());
        existingInspection.setInspector(inspection.getInspector());
        existingInspection.setInspectionType(inspection.getInspectionType());
        existingInspection.setStatus(inspection.getStatus());
        existingInspection.setFindings(inspection.getFindings());
        existingInspection.setRecommendations(inspection.getRecommendations());
        existingInspection.setNextInspectionDate(inspection.getNextInspectionDate());

        inspectionRepository.save(existingInspection);
        log.info("Successfully updated inspection with id: {}", id);
        return "redirect:/safety-inspections";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteInspection(@PathVariable Long id) {
        log.info("Deleting inspection with id: {}", id);
        inspectionRepository.deleteById(id);
        return "redirect:/safety-inspections";
    }
}