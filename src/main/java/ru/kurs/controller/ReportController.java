package ru.kurs.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kurs.entity.Report;
import ru.kurs.entity.Quarry;
import ru.kurs.entity.User;
import ru.kurs.repository.ReportRepository;
import ru.kurs.repository.WorkerRepository;
import ru.kurs.security.CustomUserDetails;
import ru.kurs.service.QuarryService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;
    private final QuarryService quarryService;
    private final WorkerRepository workerRepository;

    @GetMapping
    public String listReports(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing reports list page for user: {}", userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var reports = reportRepository.findByQuarryIn(quarries);
        log.info("Found {} reports for user", reports.size());
        model.addAttribute("reports", reports);
        return "reports";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'USER', 'BLASTING_ENGINEER')")
    @GetMapping("/new")
    public String newReportForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        log.info("Accessing new report form for user: {}", userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var workers = workerRepository.findByQuarryIn(quarries);
        log.info("Found {} workers for user's quarries", workers.size());
        model.addAttribute("report", new Report());
        model.addAttribute("quarries", quarries);
        model.addAttribute("workers", workers);
        return "report-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'USER', 'BLASTING_ENGINEER')")
    @PostMapping("/new")
    public String createReport(@ModelAttribute Report report) {
        log.info("Creating new report: {}", report);
        try {
            report.setReportDate(LocalDateTime.now());
            reportRepository.save(report);
            log.info("Successfully created report with id: {}", report.getId());
            return "redirect:/reports";
        } catch (Exception e) {
            log.error("Error creating report", e);
            throw e;
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'USER', 'BLASTING_ENGINEER')")
    @GetMapping("/edit/{id}")
    public String editReportForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        log.info("Accessing edit form for report with id: {} for user: {}", id, userDetails.getUser().getUserName());
        var quarries = quarryService.getQuarriesForUser(userDetails.getUser());
        var workers = workerRepository.findByQuarryIn(quarries);
        log.info("Found {} workers for user's quarries", workers.size());
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        model.addAttribute("report", report);
        model.addAttribute("quarries", quarries);
        model.addAttribute("workers", workers);
        return "report-form";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @PostMapping("/edit/{id}")
    public String updateReport(@PathVariable Long id, @ModelAttribute Report report) {
        log.info("Updating report with id: {}", id);
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // Обновляем все поля существующего отчета
        existingReport.setTitle(report.getTitle());
        existingReport.setContent(report.getContent());
        existingReport.setReportType(report.getReportType());
        existingReport.setQuarry(report.getQuarry());
        existingReport.setAuthor(report.getAuthor());

        reportRepository.save(existingReport);
        log.info("Successfully updated report with id: {}", id);
        return "redirect:/reports";
    }

    @GetMapping("/view/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        log.info("Viewing report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
        model.addAttribute("report", report);
        return "report-view";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadReport(@PathVariable Long id) {
        log.info("Downloading report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));

        // Формируем содержимое файла
        String content = String.format("""
                Отчет: %s
                Тип: %s
                Дата: %s
                Автор: %s %s
                Карьер: %s

                Содержание:
                %s
                """,
                report.getTitle(),
                report.getReportType(),
                report.getReportDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                report.getAuthor().getFirstName(),
                report.getAuthor().getLastName(),
                report.getQuarry().getName(),
                report.getContent());

        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_" + id + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    @GetMapping("/delete/{id}")
    public String deleteReport(@PathVariable Long id) {
        log.info("Deleting report with id: {}", id);
        reportRepository.deleteById(id);
        return "redirect:/reports";
    }
}