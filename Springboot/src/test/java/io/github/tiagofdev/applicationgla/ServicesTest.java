package io.github.tiagofdev.applicationgla;

import io.github.tiagofdev.applicationgla.model.HistoricalEntity;
import io.github.tiagofdev.applicationgla.service.Services;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ServicesTest {

    @InjectMocks
    private Services services;

    private List<HistoricalEntity> historicalEntities;

    @Before
    public void setUp() {
        // Initialize historical entities
        historicalEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HistoricalEntity entity = new HistoricalEntity();
            entity.setName("Bitcoin");
            entity.setClosePrice(BigDecimal.valueOf(1000 + i * 100));
            historicalEntities.add(entity);
        }

        services = new Services();
    }


    @Test
    public void testGetForecastSMAMock() {
        // Act
        List<List<BigDecimal>> result = services.getForecastSMA(historicalEntities, "Bitcoin");

        // Assert
        List<BigDecimal> forecastSMA = result.get(0);
        List<BigDecimal> forecastEMA = result.get(1);

        assertEquals(31, forecastSMA.size());
        assertEquals(4, forecastEMA.size());

        assertNotNull(forecastSMA);
        assertNotNull(forecastEMA);


    }

    @Test
    public void testGetProjectionsLRegressionMock() {
        // Act
        List<BigDecimal> projections = services.getProjectionsLRegression(historicalEntities, "Bitcoin");

        // Assert
        assertEquals(8, projections.size());
        assertNotNull(projections);

        // Verify the projected values

    }



    @Test
    public void testGetForecastSMA() {
        // Act
        List<List<BigDecimal>> result = services.getForecastSMA(historicalEntities, "Bitcoin");

        // Assert
        assertNotNull("Result should not be null", result);
        assertEquals("Result should contain two lists (SMA and EMA)", 2, result.size());

        List<BigDecimal> forecastSMA = result.get(0);
        List<BigDecimal> forecastEMA = result.get(1);

        // Check SMA
        assertNotNull("SMA forecast list should not be null", forecastSMA);
        assertEquals("SMA forecast list should have 31 elements (forecast period)", 31, forecastSMA.size());

        // Validate that all non-null SMA values are of type BigDecimal
        forecastSMA.forEach(value -> {
            if (value != null) assertTrue("SMA values should be BigDecimal", value instanceof BigDecimal);
        });

        // Check EMA
        assertNotNull("EMA forecast list should not be null", forecastEMA);
        assertEquals("EMA forecast list should have 4 elements (forecast period)", 4, forecastEMA.size());

        // Validate that all non-null EMA values are of type BigDecimal
        forecastEMA.forEach(value -> {
            if (value != null) assertTrue("EMA values should be BigDecimal", value instanceof BigDecimal);
        });
    }

    @Test
    public void testGetProjectionsLRegression() {
        // Act
        List<BigDecimal> projections = services.getProjectionsLRegression(historicalEntities, "Bitcoin");

        // Assert
        assertNotNull("Projections should not be null", projections);
        assertEquals("Projections should contain 8 forecast values", 8, projections.size());

        // Validate that all projection values are of type BigDecimal
        projections.forEach(value -> assertTrue("Projection values should be BigDecimal", value instanceof BigDecimal));

        // Further validation
        // Check if the projections follow the expected trend
        for (int i = 1; i < projections.size(); i++) {
            assertTrue("Projection values should be increasing",
                    projections.get(i).compareTo(projections.get(i - 1)) >= 0);
        }
    }




}



