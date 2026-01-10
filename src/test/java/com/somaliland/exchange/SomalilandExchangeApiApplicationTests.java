package com.somaliland.exchange;

import com.somaliland.exchange.model.ExchangeRate;
import com.somaliland.exchange.repository.ExchangeRateRepository;
import com.somaliland.exchange.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/db_name",
        "spring.datasource.username=postgres",
        "spring.datasource.password=enter ur password",
        "exchange.api.key=test_key",
        "app.admin.secret=test_secret"
})
class SomalilandExchangeApiApplicationTests {

    @Autowired
    private ExchangeRateService service; // Real Service

    @Autowired
    private ExchangeRateRepository repository; // Real Database Connection

    @Test
    void testRealDatabaseConversion() {
        // 1. SETUP: Save a real "Test Currency" into your local Postgres DB
        ExchangeRate testRate = new ExchangeRate();
        testRate.setCurrencyCode("TEST_USD");
        testRate.setRate(new BigDecimal("8500.00")); // Matching standard currency scale
        repository.save(testRate);

        try {
            // 2. EXECUTE: 100 * 8500 = 850,000
            BigDecimal result = service.convert("TEST_USD", new BigDecimal("100"));

            // 3. VERIFY: Check if the math matches
            // We use compareTo because "850000" and "850000.00" are technically different in Java
            // but mathematically the same. compareTo returns 0 if they are equal.
            assertEquals(0, result.compareTo(new BigDecimal("850000")));

        } finally {
            // 4. CLEANUP:
            repository.delete(testRate);
        }
    }
}