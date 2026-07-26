package com.clauseguard.repository;

import com.clauseguard.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByCancellationDeadlineBetween(LocalDate start, LocalDate end);

    List<Contract> findByRiskLevelOrderByCancellationDeadlineAsc(Contract.RiskLevel riskLevel);

    List<Contract> findAllByOrderByCancellationDeadlineAsc();
}
