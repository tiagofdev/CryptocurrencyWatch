package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.model.HistoricalEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class Services implements IServices {


    /**
     * Method to calculate Simple Moving Average (SMA) with BigDecimal AND
     * Exponential Moving Average (EMA)
     */
    public List<List<BigDecimal>> getForecastSMA(List<HistoricalEntity> historicalEntities, String name) {
         // Fixed window size for SMA
        int smaForecastDays = 31;
        int emaForecastDays = 4;
        List<HistoricalEntity> filteredEntities = historicalEntities.stream()
                .filter(historicalEntity -> historicalEntity.getName().equals(name))
                .toList();

        List<BigDecimal> prices = filteredEntities.stream().map(HistoricalEntity::getClosePrice).toList();
        int period = prices.size();
        int period3 = 7;
        List<BigDecimal> sma = new ArrayList<>();



        BigDecimal previousEMA = prices.get(0); // Start EMA with the first price

        // Calculate historical SMA
        for (int i = 0; i < prices.size(); i++) {
            // SMA
            if (i < period - 1) {
                sma.add(null); // Not enough data points to calculate SMA
            } else {
                BigDecimal sum = BigDecimal.ZERO;
                for (int j = i; j > i - period; j--) {
                    sum = sum.add(prices.get(j));
                }
                sma.add(sum.divide(BigDecimal.valueOf(period), RoundingMode.HALF_UP));
            }


        }

        List<BigDecimal> forecastSma = new ArrayList<>();
        List<BigDecimal> forecastEma = new ArrayList<>();

        // Forecast SMA for the next `forecastDays`
        List<BigDecimal> forecastPrices = new ArrayList<>(prices);
        for (int i = 0; i < smaForecastDays; i++) {
            // Assume the forecast price is the last EMA (or modify this logic for better prediction)
            forecastPrices.add(previousEMA);

            // SMA forecast
            if (forecastPrices.size() >= period) {
                BigDecimal smaSum = BigDecimal.ZERO;
                for (int j = forecastPrices.size() - 1; j >= forecastPrices.size() - period; j--) {
                    smaSum = smaSum.add(forecastPrices.get(j));
                }
                forecastSma.add(smaSum.divide(BigDecimal.valueOf(period), RoundingMode.HALF_UP));
            } else {
                forecastSma.add(null);
            }
        }


        forecastPrices = new ArrayList<>(prices);
        for (int i = 0; i < emaForecastDays; i++) {
            // Assume the forecast price is the last EMA (or modify this logic for better prediction)
            forecastPrices.add(previousEMA);

            // SMA forecast
            if (forecastPrices.size() >= period3) {
                BigDecimal smaSum = BigDecimal.ZERO;
                for (int j = forecastPrices.size() - 1; j >= forecastPrices.size() - period3; j--) {
                    smaSum = smaSum.add(forecastPrices.get(j));
                }
                forecastEma.add(smaSum.divide(BigDecimal.valueOf(period3), RoundingMode.HALF_UP));
            } else {
                forecastEma.add(null);
            }
        }



        List<List<BigDecimal>> result = new ArrayList<>();
        result.add(forecastSma); // forecast SMA
        result.add(forecastEma); // forecast EMA
        return result;
    }

    // Method to calculate linear regression coefficients (slope and intercept)
    private double[] calculateLinearRegression(List<BigDecimal> data) {
        int n = data.size();

        // Calculate sums needed for linear regression
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;

        for (int i = 0; i < n; i++) {
            double y = data.get(i).doubleValue(); // Data value

            sumX += i;
            sumY += y;
            sumXY +=  i * y;
            sumX2 += (double) i * (double) i;
        }

        // Calculate slope (m) and intercept (b) using the formulas
        double m;
        if ((n * sumX2 - sumX * sumX) != 0) {
            m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        } else {
            m = 1;
        }

        double b = (sumY - m * sumX) / n;

        return new double[]{m, b};
    }

    // Method to forecast future values based on linear regression
    public List<BigDecimal> getProjectionsLRegression(List<HistoricalEntity> historicalEntities, String name) {
        int forecastDays = 8;

        List<HistoricalEntity> filteredEntities = historicalEntities.stream()
                .filter(historicalEntity -> historicalEntity.getName().equals(name))
                .toList();

        List<BigDecimal> prices = filteredEntities.stream().map(HistoricalEntity::getClosePrice).toList();

        // Step 1: Calculate linear regression coefficients (slope and intercept)
        double[] regressionParams = calculateLinearRegression(prices);
        double slope = regressionParams[0];
        double intercept = regressionParams[1];

        // Step 2: Generate forecasts for the next 'forecastDays'
        List<BigDecimal> forecastedValues = new ArrayList<>();

        // Start forecasting from the next day after the last data point
        int startDay = prices.size();

        for (int t = startDay; t < startDay + forecastDays; t++) {
            // Apply the linear regression formula to predict the future value
            double forecastValue = slope * t + intercept;
            // Convert the forecast value to BigDecimal and add to the forecast list
            forecastedValues.add(BigDecimal.valueOf(forecastValue).setScale(2, RoundingMode.HALF_UP));
        }

        return forecastedValues;
    }

}

