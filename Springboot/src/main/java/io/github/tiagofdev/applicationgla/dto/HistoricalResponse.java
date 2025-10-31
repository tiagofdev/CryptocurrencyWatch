package io.github.tiagofdev.applicationgla.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalResponse {
    private List<DailyAsset> data;

    public List<DailyAsset> getData() {
        return data;
    }



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyAsset {
        private String id;
        private String name;
        private String volumeUsd24Hr;
        private String supply;
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




        public String getVolumeUsd24Hr() {
            return volumeUsd24Hr;
        }



        public String getSupply() {
            return supply;
        }


        public String getPriceUsd() {
            return priceUsd;
        }



    }
}
