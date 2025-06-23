package net.project.journalApp.cache;

import net.project.journalApp.entity.ConfigAPIEntry;
import net.project.journalApp.repository.ExternalApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class AppCache {

    public enum keys{
        WEATHER_API,
        QUOTE_API
    }

    @Autowired
    private ExternalApiRepository externalApiRepository;

    public Map<String,String> APPCACHE_WEATHER;
    public Map<String,String> APPCACHE_QUOTE;

    @PostConstruct
    public void init(){
        APPCACHE_WEATHER = new HashMap<>();
        APPCACHE_QUOTE = new HashMap<>();
        List<ConfigAPIEntry> all = externalApiRepository.findAll();
        for (ConfigAPIEntry configAPIEntry : all){
            String key = configAPIEntry.getKey();
            if (keys.WEATHER_API.name().equals(key)) {
                APPCACHE_WEATHER.put(key, configAPIEntry.getValue());
            }
            if (keys.QUOTE_API.name().equals(key)) {
                APPCACHE_QUOTE.put(key, configAPIEntry.getValue());
            }
        }

    }
}
