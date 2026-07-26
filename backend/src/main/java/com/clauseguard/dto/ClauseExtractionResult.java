package com.clauseguard.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseExtractionResult {

    private String vendorName;
    private String effectiveDate;      // ISO yyyy-MM-dd, nullable
    private String renewalDate;        // ISO yyyy-MM-dd, nullable
    private String cancellationDeadline; // ISO yyyy-MM-dd, nullable
    private Boolean autoRenews;
    private Integer noticePeriodDays;
    private String riskLevel;          // LOW, MEDIUM, HIGH, CRITICAL
    private String riskSummary;
    private String keyClauses;         // short bullet-style text of notable clauses

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getRenewalDate() { return renewalDate; }
    public void setRenewalDate(String renewalDate) { this.renewalDate = renewalDate; }

    public String getCancellationDeadline() { return cancellationDeadline; }
    public void setCancellationDeadline(String cancellationDeadline) { this.cancellationDeadline = cancellationDeadline; }

    public Boolean getAutoRenews() { return autoRenews; }
    public void setAutoRenews(Boolean autoRenews) { this.autoRenews = autoRenews; }

    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }

    public String getKeyClauses() { return keyClauses; }
    public void setKeyClauses(String keyClauses) { this.keyClauses = keyClauses; }
}
