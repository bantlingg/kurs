package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.Worker;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.User;
import ru.kurs.repository.WorkerRepository;
import ru.kurs.security.CustomUserDetails;
import ru.kurs.service.QuarryService;

@Controller
@RequestMapping("/workers")
@RequiredArgsConstructor
@Slf4j
public class WorkerController {
    private final WorkerRepository workerRepository;
    private final QuarryService quarryService;

    @GetMapping
    public String listWorkers(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing workers list page for user: {}", userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var workers = workerRepository.findByQuarryIn(quarries);
        model.addAttribute("workers", workers);
        return "workers";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/new")
    public String newWorkerForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing new worker form for user: {}", userDetails.getUser().getUserName());
        model.addAttribute("worker", new Worker());
        model.addAttribute("quarries", quarryService.getQuarriesForUser(userDetails.getUser()));
        return "worker-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/new")
    public String createWorker(@ModelAttribute Worker worker) {
        log.info("Creating new worker: {} {}", worker.getFirstName(), worker.getLastName());
        workerRepository.save(worker);
        return "redirect:/workers";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/edit/{id}")
    public String editWorkerForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        log.info("Accessing edit form for worker with id: {} for user: {}", id, userDetails.getUser().getUserName());
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker not found with id: " + id));
        model.addAttribute("worker", worker);
        model.addAttribute("quarries", quarryService.getQuarriesForUser(userDetails.getUser()));
        return "worker-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateWorker(@PathVariable Long id, @ModelAttribute Worker worker) {
        log.info("Updating worker with id: {}", id);
        Worker existingWorker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker not found with id: " + id));

        // Обновляем все поля существующего работника
        existingWorker.setFirstName(worker.getFirstName());
        existingWorker.setLastName(worker.getLastName());
        existingWorker.setPosition(worker.getPosition());
        existingWorker.setStatus(worker.getStatus());
        existingWorker.setQuarry(worker.getQuarry());

        workerRepository.save(existingWorker);
        log.info("Successfully updated worker with id: {}", id);
        return "redirect:/workers";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteWorker(@PathVariable Long id) {
        log.info("Deleting worker with id: {}", id);
        workerRepository.deleteById(id);
        return "redirect:/workers";
    }
}