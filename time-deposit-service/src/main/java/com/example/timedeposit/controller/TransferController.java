package com.example.timedeposit.controller;

import com.example.timedeposit.api.TransferRequest;
import com.example.timedeposit.api.TransferResponse;
import com.example.timedeposit.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferRequest req) {
        UUID tdId = service.transfer(req);
        return ResponseEntity.ok(new TransferResponse(
                req.fromAccountId(), tdId, "COMPLETED"
        ));
    }

    // おまけ：疎通確認
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
