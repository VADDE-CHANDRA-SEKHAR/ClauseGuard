package com.clauseguard.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vendorName;

    @Column(nullable = false)
    private String contractTitle;

    @Lob
    @Column(nullable = false)
    private String rawText;

    private LocalDate effectiveDate;
    private LocalDate renewalDate;
    private LocalDate cancellationDeadline;

    private Boolean autoRenews;
    private Integer noticePeriodDays;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Lob
    private String riskSummary;

    @Lob
    private String keyClausesJson;

    private LocalDateTime uploadedAt;
    private LocalDateTime analyzedAt;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL, UNKNOWN
    }

    public Contract() {
        this.uploadedAt = LocalDateTime.now();
        this.riskLevel = RiskLevel.UNKNOWN;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getContractTitle() { return contractTitle; }
    public void setContractTitle(String contractTitle) { this.contractTitle = contractTitle; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDate getRenewalDate() { return renewalDate; }
    public void setRenewalDate(LocalDate renewalDate) { this.renewalDate = renewalDate; }

    public LocalDate getCancellationDeadline() { return cancellationDeadline; }
    public void setCancellationDeadline(LocalDate cancellationDeadline) { this.cancellationDeadline = cancellationDeadline; }

    public Boolean getAutoRenews() { return autoRenews; }
    public void setAutoRenews(Boolean autoRenews) { this.autoRenews = autoRenews; }

    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }

    public String getKeyClausesJson() { return keyClausesJson; }
    public void setKeyClausesJson(String keyClausesJson) { this.keyClausesJson = keyClausesJson; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
