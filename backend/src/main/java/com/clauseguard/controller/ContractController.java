package com.clauseguard.controller;

import com.clauseguard.model.Contract;
import com.clauseguard.service.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin(origins = "*")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Contract> upload(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "vendorName", required = false) String vendorName,
                                            @RequestParam(value = "title", required = false) String title) throws IOException {
        Contract saved = contractService.uploadAndAnalyze(file, vendorName, title);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/text")
    public ResponseEntity<Contract> analyzeText(@RequestBody Map<String, String> payload) {
        String title = payload.getOrDefault("title", "Untitled contract");
        String vendorName = payload.get("vendorName");
        String rawText = payload.get("rawText");
        Contract saved = contractService.analyzeRawText(title, vendorName, rawText);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Contract>> getAll() {
        return ResponseEntity.ok(contractService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.getById(id));
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<Contract>> getAlerts(
            @RequestParam(value = "withinDays", defaultValue = "45") int withinDays) {
        return ResponseEntity.ok(contractService.getUpcomingDeadlines(withinDays));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
