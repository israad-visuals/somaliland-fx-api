package com.somaliland.exchange.service;

import com.somaliland.exchange.model.ExchangeRate;
import com.somaliland.exchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {
    private final RestTemplate restTemplate;
    private final ListableBeanFactory listableBeanFactory;
    private ExchangeRateRepository incomingrepo;
    @Value("${exchange.api.key}")
    private String apiKey;
    public ExchangeRateService(ExchangeRateRepository incomingrepo, RestTemplate restTemplate, ListableBeanFactory listableBeanFactory) {
        this.incomingrepo = incomingrepo;
        this.restTemplate = restTemplate;
        this.listableBeanFactory = listableBeanFactory;
    }
    public BigDecimal convert(String currencyCode, BigDecimal amount) {
        ExchangeRate rateEntry = incomingrepo.findByCurrencyCode(currencyCode)
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        return amount.multiply(rateEntry.getRate());
    }

    public ExchangeRate updateRate(String currencyCode, BigDecimal rate) {
      ExchangeRate entry = new ExchangeRate();
      entry.setCurrencyCode(currencyCode);
      entry.setRate(rate);
      entry.setDateFetched(LocalDateTime.now());
      return incomingrepo.save(entry);
    }
    public List<ExchangeRate> getAllRates() {
        return incomingrepo.findAll();
    }
     public BigDecimal getInternationalRate(String targetcurrency) {
        String url = "https://v6.exchangerate-api.com/v6/7a8b8834fd3c6968ce9074eb/latest/USD";
        Map response = restTemplate.getForObject(url, Map.class);

        Map<String,Number> rates = (Map<String, Number>) response.get("conversion_rates");
        Number rateValue = rates.get(targetcurrency);
        return  BigDecimal.valueOf(rateValue.doubleValue());
     }

     public BigDecimal convertSomalilandToInternational(BigDecimal amountShillinngs , String targetCurrency) {
        BigDecimal internationRate = getInternationalRate(targetCurrency);
        List<ExchangeRate> history = incomingrepo.findAll();
         if (history.isEmpty()) {
             throw new RuntimeException("No somaliland rate found in the database! Please add one first.");
         }
         ExchangeRate latestLocalEntry = history.get(history.size() -1);
         BigDecimal slshToUsdRate =  latestLocalEntry.getRate();
         BigDecimal usdAmount = amountShillinngs.divide(slshToUsdRate, 10 , RoundingMode.HALF_UP);
         BigDecimal finalAmount = usdAmount.multiply(internationRate);
         return finalAmount.setScale(2, RoundingMode.HALF_UP);
     }


    public BigDecimal convertInternationalToSomaliland(BigDecimal amountForeign, String sourceCurrency) {


        BigDecimal internationalRate = getInternationalRate(sourceCurrency);


        List<ExchangeRate> history = incomingrepo.findAll();
        if (history.isEmpty()) {
            throw new RuntimeException("No Somaliland rates found!");
        }
        ExchangeRate latestLocalEntry = history.get(history.size() - 1);
        BigDecimal slshToUsdRate = latestLocalEntry.getRate(); // e.g. 8500.00


        BigDecimal usdAmount = amountForeign.divide(internationalRate, 10, RoundingMode.HALF_UP);


        BigDecimal slshAmount = usdAmount.multiply(slshToUsdRate);


        return slshAmount.setScale(2, RoundingMode.HALF_UP);
    }
}
