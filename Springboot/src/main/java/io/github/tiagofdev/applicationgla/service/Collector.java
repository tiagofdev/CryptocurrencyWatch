package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.dto.CurrencyResponse;
import io.github.tiagofdev.applicationgla.dto.HistoricalResponse;
import io.github.tiagofdev.applicationgla.dto.PriceResponse;
import io.github.tiagofdev.applicationgla.model.*;
import io.github.tiagofdev.applicationgla.repository.CurrencyEntityRepository;
import io.github.tiagofdev.applicationgla.repository.HistoricalEntityRepository;
import io.github.tiagofdev.applicationgla.repository.PriceEntityRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Collects and calls price monitor every 60 seconds
 */
@Service
@Component
public class Collector implements ICollector {

    // This is used to contact external API
    private final RestTemplate restTemplate;
    private final ServiceAlert serviceAlert;

    private final PriceEntityRepository priceEntityRepository;
    private final HistoricalEntityRepository historicalEntityRepository;
    private final CurrencyEntityRepository currencyEntityRepository;

    private static final Logger logger = LoggerFactory.getLogger(Collector.class);

    // Constructor injection (recommended)
    @Autowired
    public Collector(PriceEntityRepository priceEntityRepository,
                     HistoricalEntityRepository historicalEntityRepository,
                     CurrencyEntityRepository currencyEntityRepository,
                     ServiceAlert serviceAlert,
                     RestTemplate restTemplate) {
        this.priceEntityRepository = priceEntityRepository;
        this.historicalEntityRepository = historicalEntityRepository;
        this.restTemplate = restTemplate;
        this.currencyEntityRepository = currencyEntityRepository;
        this.serviceAlert = serviceAlert;
    }

    /**
     * This method is run every 60 seconds.
     * It collects only the price at the current time.
     * It saves the data as PriceEntity to the DB.
     */
    @Scheduled(fixedRate = 180000) // Run every 60 seconds
    @Transactional
    public void fetchAndStoreData() {
        String url = "https://api.coincap.io/v2/assets"; // CoinCap API endpoint
        try {
            // Fetch data from API
            PriceResponse response = restTemplate.getForObject(url, PriceResponse.class);

            if (response != null && response.getData() != null) {
                response.getData().forEach(asset -> {
                    PriceEntity priceEntity = new PriceEntity();
                    priceEntity.setName(asset.getName());
                    priceEntity.setPriceUsd(new BigDecimal(asset.getPriceUsd()));
                    priceEntity.setCollectedAt(LocalDateTime.now());
                    priceEntityRepository.save(priceEntity);
                    serviceAlert.checkAlerts(priceEntity);
                });
            }
        } catch (Exception e) {
            log(e);
        }
    }

    private void log(Exception e) {
        System.err.println("Error fetching data: " + e.getMessage());
        logger.error("Error fetching data: {}", e.getMessage(), e);
    }
    int increment = 0;
    /**
     * This calls the fetch historical data method every X seconds.
     * I have to limit the number of calls I make because the server API
     * only accepts 429 requests per minute
     * I could only go as far as 3 months in the past
     */
//        @Scheduled(fixedRate = 60000)
//    @PostConstruct
    @Transactional
    public void verifyMinute() {
        if ( increment < 5 ) {
            fetchAndStoreHistoricalData();
            increment += 1;

        }

    }

    /**
     * Cryptocoin markets do not have an opening and closing time.
     * This method is supposed to run once a day, ideally by the end of the day, so that we get the highest number of
     * data collected from the day. This will enable to collect the open price, a close price, the highest price, and
     * the lowest price.
     * The data is saved as HistoricalEntity to the DB.
     */
//    @PostConstruct
    @Transactional
    public void fetchAndStoreHistoricalData() {
        String baseUrl = "https://api.coincap.io/v2/assets"; // CoinCap API endpoint


//        long end = today.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
//        long start = today.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        LocalDate theDate = LocalDate.now(ZoneOffset.UTC).minus(increment , ChronoUnit.DAYS);
        long end = theDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();
        long start = theDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();

        try { // Fetch list of all assets
            HistoricalResponse response = restTemplate.getForObject(baseUrl, HistoricalResponse.class);
            if (response != null && response.getData() != null) {
                response.getData().forEach(asset -> {
                    String assetId = asset.getId();
                    // With history, they only provide price, that's it
                    String url = String.format("https://api.coincap.io/v2/assets/%s/history?interval=h1&start=%s&end=%s", assetId, start, end);

                    // Fetch Response for historical data for each asset
                    HistoricalResponse historicalResponse = restTemplate.getForObject(url, HistoricalResponse.class);
                    // If we get a valid response
                    if (historicalResponse != null && historicalResponse.getData() != null) {
                        // new HistoricalEntity
                        HistoricalEntity historicalEntity = new HistoricalEntity();
                        // First let's check if there is already a HistoricalEntity with the name-date combination
                        Optional<HistoricalEntity> existingEntity = historicalEntityRepository.findByNameAndDate(asset.getName(), theDate);
                        // If there is, we are only going to update the existing entry
                        if (existingEntity.isPresent()) {
                            historicalEntity = existingEntity.get();
                        } else { // Otherwise, create a new entry with the name-date combination
                            historicalEntity.setName(asset.getName());
                            historicalEntity.setDate(theDate);
                        }
                        historicalEntity.setVolumeUsd24Hr(new BigDecimal(asset.getVolumeUsd24Hr()));
                        historicalEntity.setSupply(new BigDecimal(asset.getSupply()));
                        // We're going to extract the price list from today
                        List<BigDecimal> bigDecimals = new ArrayList<>();
                        historicalResponse.getData().forEach(historicalAsset -> bigDecimals.add(new BigDecimal(historicalAsset.getPriceUsd())));
                        // Calculate max and min
                        Optional<BigDecimal> max = bigDecimals.stream().max(BigDecimal::compareTo);
                        Optional<BigDecimal> min = bigDecimals.stream().min(BigDecimal::compareTo);
                        max.ifPresent(historicalEntity::setHighPrice); // Output: 20.75
                        min.ifPresent(historicalEntity::setLowPrice);
                        // OpenPrice should be the first item on the list
                        historicalEntity.setOpenPrice(bigDecimals.get(0));
                        // ClosePrice should be the last item on the list
                        historicalEntity.setClosePrice(bigDecimals.get(bigDecimals.size()-1));
                        // Save or Update the HistoricalEntity
//                        saveHistoricalEntity(historicalEntity);
                        historicalEntityRepository.save(historicalEntity);
                    }
                });
            }
        } catch (Exception e) {
            log(e);
        }
    }

    /**
     * This method is supposed to collect basic currency info from the API.
     * It saves collected data as CurrencyEntity to the DB.
     * In theory, it could run only once, but it can be run daily in order to update rank and extra info.
     * It is executed only once when SpringBoot is launched with the annotation @PostConstruct
     */
    @PostConstruct
    @Transactional
    public void getCoinInfoFromAPI() {
        String url = "https://api.coincap.io/v2/assets"; // CoinCap API endpoint
        try {
            // Fetch data from API
            CurrencyResponse response = restTemplate.getForObject(url, CurrencyResponse.class);

            if (response != null && response.getData() != null) {
                response.getData().forEach(asset -> {
                    CurrencyEntity currencyEntity = new CurrencyEntity();
                    currencyEntity.setId(asset.getId());
                    currencyEntity.setName(asset.getName());
                    currencyEntity.setExplorer(asset.getExplorer());
                    currencyEntity.setSymbol(asset.getSymbol());
                    currencyEntity.setRank(asset.getRank());
                    currencyEntityRepository.save(currencyEntity);
                });
            }
        } catch (Exception e) {
            log(e);
        }
    }


     // If you are using Spring Data JPA, the save() method in CrudRepository or JpaRepository behaves like merge():


    /**
     * This method collects CurrencyEntity from DB and sorts them by rank before returning
     * @return List<CurrencyEntity>
     */
    @Transactional(readOnly = true)
    public List<CurrencyEntity> getCurrencies() {
        List<CurrencyEntity> list = new ArrayList<>();
        list = currencyEntityRepository.findAll();
        list.sort(Comparator.comparingInt(CurrencyEntity::getRank));
        return list;
    }

    /**
     * Retrieve all PriceEntity from BD
     * @return List of PriceEntity
     */
    @Transactional(readOnly = true)
    public List<PriceEntity> getPrices() {
        return priceEntityRepository.findAll();

    }

    /**
     * Retrieve all HistoricalEntity from BD
     * @return List of HistoricalEntity
     */
    @Transactional(readOnly = true)
    public List<HistoricalEntity> getHistorical() {
        List<HistoricalEntity> list;
        list = historicalEntityRepository.findAll();
        list.sort(Comparator.comparing(HistoricalEntity::getDate));
        return list;

    }



}
