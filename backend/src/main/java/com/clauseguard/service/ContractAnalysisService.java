package com.clauseguard.service;

import com.clauseguard.dto.ClauseExtractionResult;
import com.clauseguard.model.Contract;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ContractAnalysisService {

    private final GroqClient groqClient;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
        You are a contract analysis assistant for a business operations tool called ClauseGuard.
        Extract renewal-relevant facts from the contract text the user provides and respond with
        STRICT JSON ONLY, matching exactly this schema and nothing else:

        {
          "vendorName": string,
          "effectiveDate": "YYYY-MM-DD" or null,
          "renewalDate": "YYYY-MM-DD" or null,
          "cancellationDeadline": "YYYY-MM-DD" or null,
          "autoRenews": boolean,
          "noticePeriodDays": integer or null,
          "riskLevel": one of "LOW", "MEDIUM", "HIGH", "CRITICAL",
          "riskSummary": string (1-2 sentences explaining the risk rating),
          "keyClauses": string (short bullet-style summary of notable clauses, separated by " | ")
        }

        Risk rating guide:
        - CRITICAL: auto-renews with a short/easy-to-miss notice window (under 30 days) or steep
          penalty/price-escalation on renewal.
        - HIGH: auto-renews with a moderate notice window (30-60 days).
        - MEDIUM: auto-renews with a generous notice window (60+ days), or unclear/ambiguous terms.
        - LOW: no auto-renewal, or explicit opt-in renewal required.

        If a date or field cannot be determined from the text, use null. Never invent facts not
        present in the contract text. Return JSON only, no markdown fences, no commentary.
        """;

    public ContractAnalysisService(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    public void analyze(Contract contract) {
        String userPrompt = "Contract title: " + contract.getContractTitle() +
            "\n\nContract text:\n" + truncate(contract.getRawText(), 12000);

        String rawJson = groqClient.complete(SYSTEM_PROMPT, userPrompt);

        try {
            ClauseExtractionResult result = mapper.readValue(rawJson, ClauseExtractionResult.class);
            applyResult(contract, result);
        } catch (Exception e) {
            contract.setRiskLevel(Contract.RiskLevel.UNKNOWN);
            contract.setRiskSummary("AI extraction failed to parse: " + e.getMessage());
        }
    }

    private void applyResult(Contract contract, ClauseExtractionResult r) {
        if (r.getVendorName() != null && !r.getVendorName().isBlank()) {
            contract.setVendorName(r.getVendorName());
        }
        contract.setEffectiveDate(parseDate(r.getEffectiveDate()));
        contract.setRenewalDate(parseDate(r.getRenewalDate()));
        contract.setCancellationDeadline(parseDate(r.getCancellationDeadline()));
        contract.setAutoRenews(r.getAutoRenews());
        contract.setNoticePeriodDays(r.getNoticePeriodDays());
        contract.setRiskSummary(r.getRiskSummary());
        contract.setKeyClausesJson(r.getKeyClauses());

        try {
            contract.setRiskLevel(Contract.RiskLevel.valueOf(
                r.getRiskLevel() == null ? "UNKNOWN" : r.getRiskLevel().toUpperCase()));
        } catch (IllegalArgumentException ex) {
            contract.setRiskLevel(Contract.RiskLevel.UNKNOWN);
        }
    }

    private LocalDate parseDate(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            return LocalDate.parse(iso.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        return text.length() <= maxChars ? text : text.substring(0, maxChars);
    }
}
