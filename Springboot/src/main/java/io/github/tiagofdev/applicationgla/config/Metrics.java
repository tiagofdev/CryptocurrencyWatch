package io.github.tiagofdev.applicationgla.config;

import io.prometheus.client.Counter;

/**
 *
 */
public class Metrics {
    // Create a counter metric
    /**
     *
     */
    private static final Counter requestCounter = Counter.build()
            .name("processed_requests_total") // Metric name
            .help("Total number of processed requests") // Metric description
            .register();

    /**
     *
     */
    public void processRequest() {
        // Increment the counter whenever a request is processed
        requestCounter.inc();

    }
}
