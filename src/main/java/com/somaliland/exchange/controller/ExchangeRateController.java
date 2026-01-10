package com.somaliland.exchange.controller;

import com.somaliland.exchange.service.ExchangeRateService;
import com.somaliland.exchange.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
public class ExchangeRateController {


    private final ExchangeRateService myService;

    @Value("${app.admin.secret}")
    private String adminSecret;
    public ExchangeRateController(ExchangeRateService myService) {


        this.myService = myService;
    }




    @PostMapping("/update")
    public ResponseEntity<?> updateRate(
            @RequestParam String currencyCode,
            @RequestParam BigDecimal rate,
            @RequestHeader("X-ADMIN-KEY") String adminKey
    ) {


        if (!adminSecret.equals(adminKey)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied: Wrong Key");
        }


        ExchangeRate savedEntry = myService.updateRate(currencyCode, rate);
        return ResponseEntity.ok(savedEntry);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExchangeRate>> getAllRate() {

        List<ExchangeRate> history = myService.getAllRates();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertCurrency ( @RequestParam BigDecimal amount ,
                                                        @RequestParam String target) {
        BigDecimal result = myService.convertSomalilandToInternational(amount, target);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/convert-to-slsh")
    public ResponseEntity<BigDecimal> convertToShillings(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency
    ) {
        BigDecimal result = myService.convertInternationalToSomaliland(amount, fromCurrency);
        return ResponseEntity.ok(result);
    }

}