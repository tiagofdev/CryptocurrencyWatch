package io.github.tiagofdev.applicationgla.dto;

/**
 * Class to send data back to client
 */
public class AlertsDTO {

    private String username;
    private String currency;
    private boolean alertCriteria;
    private boolean operator;
    private double threshold;
    private boolean alertedToday;

    /**
     *
     * @param username ,
     * @param currency ,
     * @param alertCriteria ,
     * @param operator ,
     * @param threshold ,
     * @param alertedToday ,
     */
    public AlertsDTO(String username, String currency, boolean alertCriteria, boolean operator, double threshold, boolean alertedToday) {
        this.username = username;
        this.currency = currency;
        this.alertCriteria = alertCriteria;
        this.operator = operator;
        this.threshold = threshold;
        this.alertedToday = alertedToday;
    }

    /**
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username ,
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return String
     */
    public String getCurrency() {
        return currency;
    }

    /**
     *
     * @param currency ,
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isAlertCriteria() {
        return alertCriteria;
    }

    public void setAlertCriteria(boolean alertCriteria) {
        this.alertCriteria = alertCriteria;
    }

    public boolean isOperator() {
        return operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isAlertedToday() {
        return alertedToday;
    }

    public void setAlertedToday(boolean alertedToday) {
        this.alertedToday = alertedToday;
    }
}
