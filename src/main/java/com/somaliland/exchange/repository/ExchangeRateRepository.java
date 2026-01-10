package com.somaliland.exchange.repository;

import com.somaliland.exchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long>
{

 Optional <ExchangeRate>findByCurrencyCode(String currencyCode);

}
