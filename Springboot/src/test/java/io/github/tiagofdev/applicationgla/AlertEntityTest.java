package io.github.tiagofdev.applicationgla;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import io.github.tiagofdev.applicationgla.model.AlertEntity;
import io.github.tiagofdev.applicationgla.model.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AlertEntityTest {

    @Mock
    private UserEntity userEntity;

    @InjectMocks
    private AlertEntity alertEntity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        alertEntity = new AlertEntity(userEntity, "USD", true, true, 100.0);
    }

    @Test
    public void testGettersAndSetters() {
        // Arrange
        alertEntity.setCurrency("EUR");
        alertEntity.setAlertCriteria(false);
        alertEntity.setOperator(false);
        alertEntity.setThreshold(200.0);
        alertEntity.setAlertedToday(true);

        // Act & Assert
        assertEquals(userEntity, alertEntity.getUserEntity());
        assertEquals("EUR", alertEntity.getCurrency());
        assertFalse(alertEntity.isAlertCriteria());
        assertFalse(alertEntity.isOperator());
        assertEquals(200.0, alertEntity.getThreshold(), 0.01);
        assertTrue(alertEntity.isAlertedToday());
    }

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        AlertEntity newAlert = new AlertEntity(userEntity, "BTC", true, false, 50000.0);

        // Act & Assert
        assertEquals(userEntity, newAlert.getUserEntity());
        assertEquals("BTC", newAlert.getCurrency());
        assertTrue(newAlert.isAlertCriteria());
        assertFalse(newAlert.isOperator());
        assertEquals(50000.0, newAlert.getThreshold(), 0.01);
        assertFalse(newAlert.isAlertedToday());
    }

    @Test
    public void testDefaultConstructor() {
        // Arrange & Act
        AlertEntity defaultAlert = new AlertEntity();

        // Assert
        assertNull(defaultAlert.getUserEntity());
        assertNull(defaultAlert.getCurrency());
        assertFalse(defaultAlert.isAlertCriteria());
        assertFalse(defaultAlert.isOperator());
        assertEquals(0.0, defaultAlert.getThreshold(), 0.01);
        assertFalse(defaultAlert.isAlertedToday());
    }
}
