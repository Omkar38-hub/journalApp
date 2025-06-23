package net.project.journalApp.service;

import net.project.journalApp.dto.WeatherResponse;
import net.project.journalApp.cache.AppCache;
import net.project.journalApp.constants.PlaceHolders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
//    ="http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String city){
        WeatherResponse weatherResponse = redisService.get("weather_of_" + city, WeatherResponse.class);
        if (weatherResponse != null) {
            return weatherResponse;
        }
        else {
            String finalAPI = appCache.APPCACHE_WEATHER.get(AppCache.keys.WEATHER_API.toString()).replace(PlaceHolders.API_KEY, apiKey).replace(PlaceHolders.CITY, city);
            WeatherResponse response = restTemplate.getForObject(finalAPI, WeatherResponse.class);
            if(response!=null){
                redisService.set("weather_of_" + city, response, 300l);
            }
            return response;
        }
    }

}
