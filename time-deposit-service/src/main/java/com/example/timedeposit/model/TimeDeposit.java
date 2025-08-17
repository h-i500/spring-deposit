package com.example.timedeposit.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "time_deposits")
public class TimeDeposit {
    public enum Status { OPEN, CLOSED }

    @Id
    private UUID id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principal;

    @Column(name = "annual_rate", nullable = false, precision = 9, scale = 6)
    private BigDecimal annualRate; // 0.015 = 1.5%

    @Column(name = "term_days", nullable = false)
    private int termDays;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "maturity_at", nullable = false)
    private Instant maturityAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        if (startAt == null) startAt = Instant.now();
        if (maturityAt == null) maturityAt = startAt.plus(termDays, ChronoUnit.DAYS);
    }

    public UUID getId() { return id; }
    public String getOwner() { return owner; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getAnnualRate() { return annualRate; }
    public int getTermDays() { return termDays; }
    public Instant getStartAt() { return startAt; }
    public Instant getMaturityAt() { return maturityAt; }
    public Status getStatus() { return status; }

    public void setOwner(String owner) { this.owner = owner; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    public void setAnnualRate(BigDecimal annualRate) { this.annualRate = annualRate; }
    public void setTermDays(int termDays) { this.termDays = termDays; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public void setMaturityAt(Instant maturityAt) { this.maturityAt = maturityAt; }
    public void setStatus(Status status) { this.status = status; }
}