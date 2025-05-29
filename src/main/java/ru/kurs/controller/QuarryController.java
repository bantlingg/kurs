package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.User;
import ru.kurs.security.CustomUserDetails;
import ru.kurs.service.QuarryService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuarryController {
    private final QuarryService quarryService;

    @GetMapping("/quarries")
    public String listQuarries(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing quarries list page for user: {}", userDetails.getUser().getUserName());
        try {
            var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
            log.info("Found {} quarries for user", quarries.size());
            model.addAttribute("quarries", quarries);
            return "quarries";
        } catch (Exception e) {
            log.error("Error accessing quarries list", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/quarries/new")
    public String newQuarryForm(Model model) {
        log.info("Accessing new quarry form");
        model.addAttribute("quarry", new Quarry());
        return "quarry-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/quarries/new")
    public String createQuarry(@ModelAttribute Quarry quarry) {
        log.info("Creating new quarry: {}", quarry.getName());
        try {
            quarryService.createQuarry(quarry);
            log.info("Quarry created successfully");
            return "redirect:/quarries";
        } catch (Exception e) {
            log.error("Error creating quarry", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/quarries/edit/{id}")
    public String editQuarryForm(@PathVariable Long id, Model model) {
        log.info("Accessing edit form for quarry with id: {}", id);
        try {
            var quarry = quarryService.getQuarryById(id);
            model.addAttribute("quarry", quarry);
            return "quarry-form";
        } catch (Exception e) {
            log.error("Error accessing quarry edit form", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/quarries/edit/{id}")
    public String updateQuarry(@PathVariable Long id, @ModelAttribute Quarry quarry) {
        log.info("Updating quarry with id: {}", id);
        try {
            quarry.setId(id);
            quarryService.updateQuarry(quarry);
            log.info("Quarry updated successfully");
            return "redirect:/quarries";
        } catch (Exception e) {
            log.error("Error updating quarry", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/quarries/delete/{id}")
    public String deleteQuarry(@PathVariable Long id) {
        log.info("Deleting quarry with id: {}", id);
        try {
            quarryService.deleteQuarry(id);
            log.info("Quarry deleted successfully");
            return "redirect:/quarries";
        } catch (Exception e) {
            log.error("Error deleting quarry", e);
            throw e;
        }
    }
}