package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.model.HistoricalEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IServices {

    // Interface for API Return types
    List<List<BigDecimal>> getForecastSMA(List<HistoricalEntity> historicalEntities, String name);

    List<BigDecimal> getProjectionsLRegression(List<HistoricalEntity> historicalEntities, String name);




}
