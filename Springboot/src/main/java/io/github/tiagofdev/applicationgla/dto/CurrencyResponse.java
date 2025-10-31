package io.github.tiagofdev.applicationgla.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyResponse {
    private List<CoinAsset> data;

    public List<CoinAsset> getData() {
        return data;
    }



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoinAsset {
        private String id;
        private String name;
        private String symbol;
        private String explorer;
        private int rank;

        public int getRank() {
            return rank;
        }



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


        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getExplorer() {
            return explorer;
        }


    }
}
