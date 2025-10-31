package io.github.tiagofdev.applicationgla.service;


import io.github.tiagofdev.applicationgla.dto.AlertsDTO;
import io.github.tiagofdev.applicationgla.model.*;

import io.github.tiagofdev.applicationgla.repository.AlertEntityRepository;
import io.github.tiagofdev.applicationgla.repository.PriceEntityRepository;
import io.github.tiagofdev.applicationgla.repository.UserEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceAlert implements IServiceAlert {

    private final JavaMailSender mailSender;

    final AlertEntityRepository alertEntityRepository;
    final UserEntityRepository userEntityRepository;
    private final PriceEntityRepository priceEntityRepository;
    private static final Logger logger = LoggerFactory.getLogger(ServiceAlert.class);

    public ServiceAlert(AlertEntityRepository alertEntityRepository,
                        UserEntityRepository userEntityRepository,
                        JavaMailSender mailSender,
                        PriceEntityRepository priceEntityRepository) {
        this.mailSender = mailSender;
        this.userEntityRepository = userEntityRepository;
        this.alertEntityRepository = alertEntityRepository;
        this.priceEntityRepository = priceEntityRepository;
    }

    public void checkAlerts(PriceEntity priceEntity) {
        // I'm not going to implement Optional because if the customer subscribed to the currency, it's most likely
        // we should be able to find the currency in the list
        List<AlertEntity> alertEntities = alertEntityRepository.findByCurrency(priceEntity.getName());
//        Optional<HistoricalEntity> existingEntity = historicalEntityRepository.findByNameAndDate(asset.getName(), theDate);

        // Each AlertEntity
        alertEntities.forEach(alertEntity -> {
            boolean shouldAlert = false;

            // AlertEntity will be sent only once a day
            if (!alertEntity.isAlertedToday()) {
                // Check each alertEntity option individually
                // First Threshold Price
                // isAlertCriteria returns 1 for Threshold 0 for Percentage
                if (alertEntity.isAlertCriteria()) {
                    if (alertEntity.getThreshold() != 0 && alertEntity.getThreshold() > priceEntity.getPriceUsd().doubleValue()) {
                        if (alertEntity.isOperator()) {
                            shouldAlert = true;
                        }
                    } else {
                        if (!alertEntity.isOperator()) {
                            shouldAlert = true;
                        }
                    }
                } else {

                    // Check for percentage change
                    if (alertEntity.getThreshold() != 0) {
                        LocalDateTime startOfToday = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
                        LocalDateTime now = LocalDateTime.now();
                        List<PriceEntity> pricesToday = priceEntityRepository.findByNameDateRange(priceEntity.getName(), now, startOfToday);
                        double openingPrice = pricesToday.get(0).getPriceUsd().doubleValue();
                        double lastPrice = pricesToday.get(pricesToday.size()-1).getPriceUsd().doubleValue();
                        double percentageChange = (lastPrice - openingPrice) / (openingPrice * 100);
                        if (percentageChange > alertEntity.getThreshold()) {
                            if (alertEntity.isOperator()) {
                                shouldAlert = true;
                            }
                        } else {
                            if (!alertEntity.isOperator()) {
                                shouldAlert = true;
                            }
                        }
                    }
                }

                if (shouldAlert) {
                    sendEmail(alertEntity.getUserEntity().getUsername(), priceEntity, alertEntity);
                    alertEntity.setAlertedToday(true);
                }
            }
        });
    }

    public void testEmail() {

        String user = "afluentis@live.com";
        PriceEntity price = new PriceEntity();
        AlertEntity alert = new AlertEntity();
        sendEmail(user, price, alert);

    }

    private void sendEmail(String to, PriceEntity priceEntity, AlertEntity alertEntity) {
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append(String.format("Dear %s,\nWe're writing to inform you that there's been a change in the " +
                        "prices for the items you " +
                "are monitoring.\nItem: %s\nCurrent Price: %s\n"
                , to
                , priceEntity.getName()
                , priceEntity.getPriceUsd()
        ));

        if (alertEntity.isAlertCriteria()) {
            msgBuilder.append(String.format("Threshold Price: %s\n", alertEntity.getThreshold()));
        } else {
            msgBuilder.append(String.format("Percentage Change: %s\n", alertEntity.getThreshold()));
        }
        msgBuilder.append("This alert was triggered because the price has [increased/decreased] beyond your set " +
                "threshold.\n" +
                "\n" +
                "If you have any questions or need further assistance, feel free to reach out to us at " +
                "cryptosurge@yahoo.com.\n" +
                "\n" +
                "Thank you for using our Price Alert System. We value your support and look forward to keeping you " +
                "informed.\n\n" + "Best regards, CryptoSurge Team");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Price AlertEntity for " + alertEntity.getCurrency());
            helper.setText(msgBuilder.toString());

            mailSender.send(message);
        } catch (MessagingException e) {
            log(e);
        }
    }

    /**
     * This method will reset daily alerts
     */
    @Scheduled(fixedRate = 86400000)
    public void resetDailyAlerts() {
        List<AlertEntity> alertEntities = alertEntityRepository.findAll();
        alertEntities.forEach(alertEntity -> alertEntity.setAlertedToday(false));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Ensures all the operations are committed together in a transaction
    public boolean saveAlertEntity(String username, String currency, String subscribe, String criteria,
                                String operator, String threshold) {

        try {
            boolean bSubscribe = false;
            boolean bCriteria = false;
            boolean bOperator = false;

            if (subscribe.equals("1")) {
                bSubscribe = true;
            }
            if (criteria.equals("1")) {
                bCriteria = true;
            }
            if (operator.equals("1")) {
                bOperator = true;
            }

            UserEntity userEntity = userEntityRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("UserEntity not found"));
            List<AlertEntity> userAlerts = alertEntityRepository.findByUserEntityUsername(username);
            // First, Find if there is any alert for that currency
            // If there is none, create a new alert
            // If there is, overwrite any settings
            // If the setting is to unsubscribe, remove the alert from the set
            if(bSubscribe) {
                boolean found = false;
                for(AlertEntity alert : userAlerts) {
                    // If found, Update
                    if (alert.getCurrency().equals(currency)) {
                        alert.setAlertCriteria(bCriteria);
                        alert.setThreshold(Double.parseDouble(threshold));
                        alert.setOperator(bOperator);
                        found = true;
                        break;
                    }
                }
                if (!found) {

                    // Save new AlertEntity Entry if none is found with that name
                    alertEntityRepository.save(new AlertEntity(userEntity, currency, bCriteria, bOperator, Double.parseDouble(threshold)));
                }
            } else {
                for(AlertEntity alert : userAlerts) {
                    // If found, Remove
                    if (alert.getCurrency().equals(currency)) {
                        alertEntityRepository.delete(alert);
                        break;
                    }
                }
            }
            return true;
        } catch (IllegalArgumentException e) {

            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<AlertsDTO> getAlertsForUser(String username) {
        List<AlertsDTO> list = new ArrayList<>();
        alertEntityRepository.findByUserEntityUsername(username).forEach( alertEntity -> {
            AlertsDTO newAlert = new AlertsDTO(username, alertEntity.getCurrency(), alertEntity.isAlertCriteria(),
                    alertEntity.isOperator(), alertEntity.getThreshold(), alertEntity.isAlertedToday());
            list.add(newAlert);
        });
        return list;
    }

    private void log(Exception e) {
        logger.error("Error fetching data: {}", e.getMessage(), e);
    }

}
