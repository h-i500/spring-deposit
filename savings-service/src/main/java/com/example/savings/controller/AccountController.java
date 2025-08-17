package com.example.savings.controller;

import com.example.savings.model.Account;
import com.example.savings.service.AccountService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService service;

    public AccountController(AccountService service) { this.service = service; }

    public record CreateAccountRequest(@NotBlank String owner) {}
    public record MoneyRequest(@NotNull BigDecimal amount) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest req) {
        Account a = service.create(req.owner());
        return ResponseEntity.ok(Map.of(
                "id", a.getId(),
                "owner", a.getOwner(),
                "balance", a.getBalance(),
                "createdAt", a.getCreatedAt()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        Account a = service.get(id);
        return ResponseEntity.ok(Map.of(
                "id", a.getId(),
                "owner", a.getOwner(),
                "balance", a.getBalance(),
                "createdAt", a.getCreatedAt()
        ));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable UUID id, @RequestBody MoneyRequest req) {
        Account a = service.deposit(id, req.amount());
        return ResponseEntity.ok(Map.of(
                "id", a.getId(),
                "balance", a.getBalance()
        ));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable UUID id, @RequestBody MoneyRequest req) {
        Account a = service.withdraw(id, req.amount());
        return ResponseEntity.ok(Map.of(
                "id", a.getId(),
                "balance", a.getBalance()
        ));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}