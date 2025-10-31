package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.dto.AlertsDTO;
import io.github.tiagofdev.applicationgla.model.PriceEntity;

import java.util.List;

public interface IServiceAlert {

    void checkAlerts(PriceEntity priceEntity);

    boolean saveAlertEntity(String username, String currency, String subscribe, String criteria,
                                   String signal, String threshold);

    List<AlertsDTO> getAlertsForUser(String username);
}
