package com.clauseguard.service;

import com.clauseguard.model.Contract;
import com.clauseguard.repository.ContractRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository repository;
    private final ContractAnalysisService analysisService;
    private final PdfTextExtractor pdfTextExtractor;

    public ContractService(ContractRepository repository,
                            ContractAnalysisService analysisService,
                            PdfTextExtractor pdfTextExtractor) {
        this.repository = repository;
        this.analysisService = analysisService;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    public Contract uploadAndAnalyze(MultipartFile file, String vendorNameHint, String titleHint) throws IOException {
        String text = pdfTextExtractor.extract(file);

        Contract contract = new Contract();
        contract.setContractTitle(titleHint != null && !titleHint.isBlank() ? titleHint : file.getOriginalFilename());
        contract.setVendorName(vendorNameHint != null && !vendorNameHint.isBlank() ? vendorNameHint : "Unknown");
        contract.setRawText(text);

        analysisService.analyze(contract);
        contract.setAnalyzedAt(LocalDateTime.now());

        return repository.save(contract);
    }

    public Contract analyzeRawText(String title, String vendorHint, String rawText) {
        Contract contract = new Contract();
        contract.setContractTitle(title);
        contract.setVendorName(vendorHint != null && !vendorHint.isBlank() ? vendorHint : "Unknown");
        contract.setRawText(rawText);

        analysisService.analyze(contract);
        contract.setAnalyzedAt(LocalDateTime.now());

        return repository.save(contract);
    }

    public List<Contract> getAll() {
        return repository.findAllByOrderByCancellationDeadlineAsc();
    }

    public Contract getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("Contract not found: " + id));
    }

    public List<Contract> getUpcomingDeadlines(int withinDays) {
        LocalDate today = LocalDate.now();
        return repository.findByCancellationDeadlineBetween(today, today.plusDays(withinDays));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
