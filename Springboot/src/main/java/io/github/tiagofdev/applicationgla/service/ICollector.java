package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.model.CurrencyEntity;
import io.github.tiagofdev.applicationgla.model.HistoricalEntity;
import io.github.tiagofdev.applicationgla.model.PriceEntity;

import java.util.List;

public interface ICollector {

    List<CurrencyEntity> getCurrencies();

    List<PriceEntity> getPrices();

    List<HistoricalEntity> getHistorical();


}
