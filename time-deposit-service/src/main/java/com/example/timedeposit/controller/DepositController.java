package com.example.timedeposit.controller;

import com.example.timedeposit.model.TimeDeposit;
import com.example.timedeposit.service.TimeDepositService;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/deposits")
public class DepositController {
    private final TimeDepositService service;

    public DepositController(TimeDepositService service) { this.service = service; }

    public record CreateRequest(@NotBlank String owner,
                                @NotNull @DecimalMin("0.01") BigDecimal principal,
                                @NotNull @DecimalMin("0.0") BigDecimal annualRate,
                                @Min(1) int termDays) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest req) {
        TimeDeposit td = service.create(req.owner(), req.principal(), req.annualRate(), req.termDays());
        return ResponseEntity.ok(Map.of(
                "id", td.getId(),
                "owner", td.getOwner(),
                "principal", td.getPrincipal(),
                "annualRate", td.getAnnualRate(),
                "termDays", td.getTermDays(),
                "startAt", td.getStartAt(),
                "maturityDate", td.getMaturityAt(),
                "status", td.getStatus()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        TimeDeposit td = service.get(id);
        return ResponseEntity.ok(Map.of(
                "id", td.getId(),
                "owner", td.getOwner(),
                "principal", td.getPrincipal(),
                "annualRate", td.getAnnualRate(),
                "termDays", td.getTermDays(),
                "startAt", td.getStartAt(),
                "maturityDate", td.getMaturityAt(),
                "status", td.getStatus()
        ));
    }

    // @PostMapping("/{id}/close")
    // public ResponseEntity<?> close(@PathVariable UUID id) {
    //     var payout = service.close(id, Instant.now());
    //     return ResponseEntity.ok(Map.of("id", id, "payout", payout));
    // }
    @PostMapping("/{id}/close")
    public ResponseEntity<?> close(@PathVariable UUID id,
                               @RequestParam(name = "at", required = false) String atIso) {
    var now = (atIso == null) ? Instant.now() : Instant.parse(atIso);
    var payout = service.close(id, now);
    return ResponseEntity.ok(Map.of("id", id, "payout", payout));
}


    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}