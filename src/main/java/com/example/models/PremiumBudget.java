package com.example.models;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "premiumBudget",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "month")
        })
public class PremiumBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Calendar month;

    private float amount;

    public PremiumBudget() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getMonth() {
        return month;
    }

    public void setMonth(Calendar month) {
        this.month = month;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
