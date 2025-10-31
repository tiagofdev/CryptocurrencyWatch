package io.github.tiagofdev.applicationgla;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import io.github.tiagofdev.applicationgla.dto.AlertsDTO;
import io.github.tiagofdev.applicationgla.model.AlertEntity;
import io.github.tiagofdev.applicationgla.model.PriceEntity;
import io.github.tiagofdev.applicationgla.model.UserEntity;
import io.github.tiagofdev.applicationgla.repository.AlertEntityRepository;
import io.github.tiagofdev.applicationgla.repository.PriceEntityRepository;
import io.github.tiagofdev.applicationgla.repository.UserEntityRepository;
import io.github.tiagofdev.applicationgla.service.ServiceAlert;
import jakarta.mail.internet.MimeMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ServiceAlertTest {


    private AlertEntityRepository alertEntityRepository;


    private UserEntityRepository userEntityRepository;


    private PriceEntityRepository priceEntityRepository;


    private JavaMailSender mailSender;

    private ServiceAlert serviceAlert;

    @Before
    public void setUp() {

        alertEntityRepository = mock(AlertEntityRepository.class);
        userEntityRepository = mock(UserEntityRepository.class);
        priceEntityRepository = mock(PriceEntityRepository.class);

        // Initialize the ServiceAlert instance
        serviceAlert = new ServiceAlert(alertEntityRepository, userEntityRepository, null, priceEntityRepository);
    }



    @Test
    public void testSaveAlertEntity_withValidParameters() {
        // Arrange
        String username = "user@example.com";
        String currency = "Bitcoin";
        String subscribe = "1"; // Subscribe
        String criteria = "1"; // Threshold-based
        String operator = "1"; // Greater than threshold
        String threshold = "1200";

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);

        List<AlertEntity> userAlerts = new ArrayList<>();

        when(userEntityRepository.findByUsername(username)).thenReturn(java.util.Optional.of(userEntity));
        when(alertEntityRepository.findByUserEntityUsername(username)).thenReturn(userAlerts);

        // Act
        boolean result = serviceAlert.saveAlertEntity(username, currency, subscribe, criteria, operator, threshold);

        // Assert
        assertTrue("Alert should be saved successfully", result);
        verify(alertEntityRepository, times(1)).save(any(AlertEntity.class));
    }

    @Test
    public void testGetAlertsForUser_withExistingAlerts() {
        // Arrange
        String username = "user@example.com";
        AlertEntity alertEntity = new AlertEntity();
        alertEntity.setCurrency("Bitcoin");
        alertEntity.setAlertCriteria(true);
        alertEntity.setOperator(true);
        alertEntity.setThreshold(1200);
        alertEntity.setAlertedToday(false);

        List<AlertEntity> alertEntities = new ArrayList<>();
        alertEntities.add(alertEntity);

        when(alertEntityRepository.findByUserEntityUsername(username)).thenReturn(alertEntities);

        // Act
        List<AlertsDTO> alerts = serviceAlert.getAlertsForUser(username);

        // Assert
        assertNotNull("Alerts list should not be null", alerts);
        assertEquals("There should be one alert", 1, alerts.size());
        assertEquals("Currency should match", "Bitcoin", alerts.get(0).getCurrency());
    }

    @Test
    public void testResetDailyAlerts() {
        // Arrange
        AlertEntity alertEntity1 = new AlertEntity();
        alertEntity1.setAlertedToday(true);

        AlertEntity alertEntity2 = new AlertEntity();
        alertEntity2.setAlertedToday(true);

        List<AlertEntity> alertEntities = new ArrayList<>();
        alertEntities.add(alertEntity1);
        alertEntities.add(alertEntity2);

        when(alertEntityRepository.findAll()).thenReturn(alertEntities);

        // Act
        serviceAlert.resetDailyAlerts();

        // Assert
        assertFalse("Alert 1 should be reset", alertEntity1.isAlertedToday());
        assertFalse("Alert 2 should be reset", alertEntity2.isAlertedToday());
    }

}


