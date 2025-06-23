package net.project.journalApp.service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SentimentAnalysisService {
    private final StanfordCoreNLP pipeline;

    public SentimentAnalysisService() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) return "Neutral";

        CoreDocument doc = new CoreDocument(text);
        pipeline.annotate(doc);

        Map<String, Integer> sentimentCount = new HashMap<>();

        for (CoreSentence sentence : doc.sentences()) {
            String sentiment = sentence.sentiment(); // e.g., "Negative"
            sentimentCount.put(sentiment, sentimentCount.getOrDefault(sentiment, 0) + 1);
        }

        // Get dominant sentiment
        String dominant = sentimentCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Neutral");

        return mapToEmotion(dominant);
    }

    private String mapToEmotion(String sentiment) {
        switch (sentiment) {
            case "Very Negative":
                return "Angry";
            case "Negative":
                return "Sad";
            case "Neutral":
                return "Calm";
            case "Positive":
                return "Pleasant";
            case "Very Positive":
                return "Happy";
            default:
                return "Neutral";
        }
    }
}
