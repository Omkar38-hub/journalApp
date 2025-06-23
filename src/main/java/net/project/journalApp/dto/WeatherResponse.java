package net.project.journalApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;

@Getter
@Setter
public class WeatherResponse {

    private Current current;

    @Getter
    @Setter
    public class Current{
        private int temperature;

        public int getTemperature() {
            return temperature;
        }

        @JsonProperty("weather_descriptions")
        public List<String> weatherDescriptions;
    }
}
