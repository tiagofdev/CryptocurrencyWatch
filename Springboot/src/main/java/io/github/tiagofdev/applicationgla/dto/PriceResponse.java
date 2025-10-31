package io.github.tiagofdev.applicationgla.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceResponse {
    private List<Asset> data;

    public List<Asset> getData() {
        return data;
    }

    public void setData(List<Asset> data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Asset {
        private String id;
        private String name;
        private String priceUsd;



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPriceUsd() {
            return priceUsd;
        }

        public void setPriceUsd(String priceUsd) {
            this.priceUsd = priceUsd;
        }


    }
}
