package net.project.journalApp.service;
// QuoteService.java
import net.project.journalApp.cache.AppCache;
import net.project.journalApp.dto.QuoteApiResponse;
import net.project.journalApp.dto.QuoteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;

@Service
public class QuoteService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AppCache appCache;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String QUOTE_KEY = "quote:" + LocalDate.now(); // unique key per day

    public QuoteResponse getQuoteOfTheDay() {
        // Step 1: Check Redis
        String cachedQuote = redisTemplate.opsForValue().get(QUOTE_KEY);
        if (cachedQuote != null) {
            // Split on the " — " separator
            if (cachedQuote.contains(" — ")) {
                String[] parts = cachedQuote.split(" — ", 2);
                String quoteText = parts[0].replaceAll("^\"|\"$", ""); // remove leading/trailing quotes
                String author = parts[1];
                return new QuoteResponse(quoteText, author);
            }
            return new QuoteResponse(cachedQuote, "Unknown");
        }

        // Step 2: Fetch from API
        String finalAPI = appCache.APPCACHE_QUOTE.get(AppCache.keys.QUOTE_API.toString());
        QuoteApiResponse response = restTemplate.getForObject(finalAPI, QuoteApiResponse.class);

        if (response != null && response.getQuote() != null) {
            String body = response.getQuote().getBody();
            String author = response.getQuote().getAuthor();
            String formattedQuote = "\"" + body + "\" — " + author;

            // Step 3: Store in Redis
            redisTemplate.opsForValue().set(QUOTE_KEY, formattedQuote, Duration.ofHours(24));

            return new QuoteResponse(body, author);
        } else {
            return new QuoteResponse("Could not fetch quote.", "Unknown");
        }
    }
}


