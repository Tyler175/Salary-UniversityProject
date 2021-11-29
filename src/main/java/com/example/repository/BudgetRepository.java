package com.example.repository;

import com.example.models.PremiumBudget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Calendar;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<PremiumBudget, Long> {
    Optional<PremiumBudget> findByMonth(Calendar month);
}
