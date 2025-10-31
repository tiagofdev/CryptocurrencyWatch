/*
Backend API Endpoints

 */

package io.github.tiagofdev.applicationgla.controller;

import io.github.tiagofdev.applicationgla.service.Collector;
import io.github.tiagofdev.applicationgla.dto.Response;
import io.github.tiagofdev.applicationgla.service.ServiceAlert;
import io.github.tiagofdev.applicationgla.service.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
@RestController
@RequestMapping( "/")
@RequiredArgsConstructor
public class Controller {

    /**
     *
     */
    private final Services services;
    /**
     *
     */
    private final ServiceAlert serviceAlert;
    /**
     *
     */
    private final Collector collector;


    /**
     * Endpoint getCurrencies
     * @return List of CurrencyEntity
     */
//    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/getCurrencies")
    public ResponseEntity<Response> getCurrencies() {
        return ResponseEntity.ok(
            Response.builder()
                .timeStamp(now())
                .status(OK)
                .statusCode(OK.value())
                .message("Success - Currencies Retrieved")
                .data(Map.of("results", collector.getCurrencies()))
                .build()
        );
    }

    /**
     * Endpoint getPrices
     * @return List of PriceEntity
     */

    /**
     *
     * @return ResponseEntity<Response>
     */
    @GetMapping("/getPrices")
    public ResponseEntity<Response> getPrices() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Prices Retrieved")
                        .data(Map.of("results", collector.getPrices()))
                        .build()
        );
    }


    /**
     * Endpoint getHistorical
     * @return List of HistoricalEntity
     */
    /**
     *
     * @return ResponseEntity<Response>
     */
    @GetMapping("/getHistorical")
    public ResponseEntity<Response> getHistorical() {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Historical Retrieved")
                        .data(Map.of("results", collector.getHistorical()))
                        .build()
        );
    }

    /**
     *
     * @param name ,
     * @return ResponseEntity<Response>
     */
    @GetMapping("/getProjectionsSMA/{inputName}")
    public ResponseEntity<Response> getProjectionsSMA(@PathVariable("inputName") String name) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Historical Retrieved")
                        .data(Map.of("result", services.getForecastSMA(this.collector.getHistorical(), name)))
                        .build()
        );
    }

    /**
     *
     * @param name ,
     * @return ResponseEntity<Response>
     */
    @GetMapping("/getProjectionsLR/{inputName}")
    public ResponseEntity<Response> getProjectionsLRegression(@PathVariable("inputName") String name) {
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Historical Retrieved")
                        .data(Map.of("result", services.getProjectionsLRegression(this.collector.getHistorical(), name)))
                        .build()
        );
    }


    /**
     *
     * @param username ,
     * @param currency ,
     * @param criteria ,
     * @param subscription ,
     * @param operator ,
     * @param threshold ,
     * @return ResponseEntity<Response>
     */
    @GetMapping("/saveAlerts/{username}+{currency}+{subscription}+{criteria}+{operator}+{threshold}")
    public ResponseEntity<Response> saveAlerts(@PathVariable("username") String username,
                                               @PathVariable("currency") String currency,
                                               @PathVariable("criteria") String criteria,
                                               @PathVariable("subscription") String subscription,
                                               @PathVariable("operator") String operator,
                                               @PathVariable("threshold") String threshold) {


        serviceAlert.saveAlertEntity(username, currency, subscription, criteria, operator, threshold);
        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Alerts Retrieved")
                        .data(Map.of("results", serviceAlert.getAlertsForUser(username)))
                        .build()
        );
    }

    /**
     *
     * @param username ,
     * @return ResponseEntity<Response>
     */
    @GetMapping("/getAlerts/{username}")
    public ResponseEntity<Response> getAlerts(@PathVariable("username") String username) {

        return ResponseEntity.ok(
                Response.builder()
                        .timeStamp(now())
                        .status(OK)
                        .statusCode(OK.value())
                        .message("Success - Alerts Retrieved")
                        .data(Map.of("results", serviceAlert.getAlertsForUser(username)))
                        .build()
        );
    }

    /**
     *
     * @return ResponseEntity<Response>
     */
    @GetMapping("/test")
    public ResponseEntity getAlerts() {

        serviceAlert.testEmail();
        return ResponseEntity.ok("Hello from server");
    }

}
