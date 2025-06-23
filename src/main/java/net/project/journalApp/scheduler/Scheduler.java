package net.project.journalApp.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.project.journalApp.cache.AppCache;
import net.project.journalApp.entity.EmailDetails;
import net.project.journalApp.entity.JournalEntry;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.EmailServiceImpl;
import net.project.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Scheduler {

    @Autowired
    private AppCache appCache;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private UserService userService;

    /**
     * Weekly sentiment analysis and email report
     * Runs every Sunday at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * SUN")
    public void weeklySentimentAnalysisAndEmail() {
        log.info("Starting weekly sentiment analysis and email report...");

        try {
            List<UserEntry> allUsers = userService.getAll();

            for (UserEntry user : allUsers) {
                // Check if user has email and sentiment analysis is enabled
                if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && user.isSentimentAnalysis()) {
                    sendWeeklySentimentReport(user);
                }
            }

            log.info("Weekly sentiment analysis and email report completed successfully.");
        } catch (Exception e) {
            log.error("Error in weekly sentiment analysis and email report: ", e);
        }
    }

    /**
     * Send weekly sentiment report to user
     */
    private void sendWeeklySentimentReport(UserEntry user) {
        try {
            LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
            List<JournalEntry> userJournals = user.getJournalEntryList();

            if (userJournals == null || userJournals.isEmpty()) {
                sendNoJournalEmail(user);
                return;
            }

            // Filter journals from last 7 days
            List<JournalEntry> recentJournals = userJournals.stream()
                    .filter(journal -> journal.getDate() != null && journal.getDate().isAfter(sevenDaysAgo))
                    .collect(Collectors.toList());

            if (recentJournals.isEmpty()) {
                sendNoJournalEmail(user);
                return;
            }

            // Analyze sentiment counts
            Map<String, Integer> sentimentCounts = analyzeSentimentCounts(recentJournals);

            // Generate email content
            String emailContent = generateSentimentReportEmail(user.getUsername(), sentimentCounts,
                    recentJournals.size());

            // Send email
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("📊 Your Weekly Journal Sentiment Report")
                    .msgBody(emailContent)
                    .build();

            String status = emailService.sendSimpleMail(emailDetails);
            log.info("Weekly sentiment report sent to {}: {}", user.getUsername(), status);

        } catch (Exception e) {
            log.error("Error sending weekly sentiment report to user {}: ", user.getUsername(), e);
        }
    }

    /**
     * Analyze sentiment counts from journal entries
     */
    private Map<String, Integer> analyzeSentimentCounts(List<JournalEntry> journals) {
        Map<String, Integer> sentimentCounts = new HashMap<>();

        // Initialize all sentiment types
        sentimentCounts.put("HAPPY", 0);
        sentimentCounts.put("PLEASANT", 0);
        sentimentCounts.put("CALM", 0);
        sentimentCounts.put("NEUTRAL", 0);
        sentimentCounts.put("SAD", 0);
        sentimentCounts.put("ANGRY", 0);

        // Count sentiments
        for (JournalEntry journal : journals) {
            if (journal.getSentiment() != null) {
                String sentiment = journal.getSentiment().toUpperCase();
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
        }

        return sentimentCounts;
    }

    /**
     * Generate email content for sentiment report
     */
    private String generateSentimentReportEmail(String username, Map<String, Integer> sentimentCounts,
            int totalEntries) {
        StringBuilder emailContent = new StringBuilder();

        emailContent.append("Hello ").append(username).append(",\n\n");
        emailContent.append("Here's your weekly journal sentiment analysis report:\n\n");

        emailContent.append("📅 Period: Last 7 days\n");
        emailContent.append("📝 Total Journal Entries: ").append(totalEntries).append("\n\n");

        emailContent.append("🎭 Sentiment Breakdown:\n");
        emailContent.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Add sentiment counts with emojis
        emailContent.append("😊 Happy: ").append(sentimentCounts.get("HAPPY")).append(" entries\n");
        emailContent.append("😌 Pleasant: ").append(sentimentCounts.get("PLEASANT")).append(" entries\n");
        emailContent.append("😐 Calm: ").append(sentimentCounts.get("CALM")).append(" entries\n");
        emailContent.append("😶 Neutral: ").append(sentimentCounts.get("NEUTRAL")).append(" entries\n");
        emailContent.append("😔 Sad: ").append(sentimentCounts.get("SAD")).append(" entries\n");
        emailContent.append("😠 Angry: ").append(sentimentCounts.get("ANGRY")).append(" entries\n\n");

        // Find dominant sentiment
        String dominantSentiment = findDominantSentiment(sentimentCounts);
        emailContent.append("🌟 Most Frequent Mood: ").append(dominantSentiment).append("\n\n");

        // Add motivational message based on dominant sentiment
        emailContent.append("💭 Reflection:\n");
        emailContent.append(getMotivationalMessage(dominantSentiment, totalEntries));

        emailContent.append("\n\nKeep writing and reflecting! 📖✨\n");
        emailContent.append("Best regards,\n");
        emailContent.append("Your Journal App Team\n");

        return emailContent.toString();
    }

    /**
     * Find the most frequent sentiment
     */
    private String findDominantSentiment(Map<String, Integer> sentimentCounts) {
        String dominantSentiment = "NEUTRAL";
        int maxCount = 0;

        for (Map.Entry<String, Integer> entry : sentimentCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                dominantSentiment = entry.getKey();
            }
        }

        return dominantSentiment;
    }

    /**
     * Get motivational message based on dominant sentiment
     */
    private String getMotivationalMessage(String dominantSentiment, int totalEntries) {
        switch (dominantSentiment) {
            case "HAPPY":
                return "You've been experiencing a lot of joy lately! Keep spreading that positivity and continue to celebrate the good moments in your life.";
            case "PLEASANT":
                return "Your mood has been quite pleasant this week. You're finding balance and contentment in your daily experiences.";
            case "CALM":
                return "You've maintained a peaceful state of mind. This calmness is a great foundation for making thoughtful decisions.";
            case "NEUTRAL":
                return "You've been in a balanced state this week. Sometimes neutrality is exactly what we need to process our thoughts clearly.";
            case "SAD":
                return "It's okay to feel sad sometimes. Remember that difficult emotions are part of being human. Consider reaching out to friends or trying activities that usually bring you comfort.";
            case "ANGRY":
                return "You've been experiencing some frustration lately. It's important to acknowledge these feelings. Consider what might be causing this and what steps you could take to address it.";
            default:
                return "Thank you for taking the time to reflect on your emotions through journaling. Self-awareness is a powerful tool for personal growth.";
        }
    }

    /**
     * Send email for users who haven't posted in 7 days
     */
    private void sendNoJournalEmail(UserEntry user) {
        try {
            String emailContent = "Hello " + user.getUsername() + ",\n\n" +
                    "We noticed that you haven't posted any journal entries in the last 7 days.\n\n" +
                    "📝 Journaling is a great way to:\n" +
                    "• Reflect on your thoughts and feelings\n" +
                    "• Track your emotional patterns\n" +
                    "• Process daily experiences\n" +
                    "• Improve self-awareness\n\n" +
                    "Take a few minutes today to write about your day, your thoughts, or anything that's on your mind.\n\n"
                    +
                    "Even a short entry can make a difference! ✨\n\n" +
                    "Best regards,\n" +
                    "Your Journal App Team";

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("📖 Time to Journal - We Miss You!")
                    .msgBody(emailContent)
                    .build();

            String status = emailService.sendSimpleMail(emailDetails);
            log.info("No journal reminder sent to {}: {}", user.getUsername(), status);

        } catch (Exception e) {
            log.error("Error sending no journal reminder to user {}: ", user.getUsername(), e);
        }
    }

    /**
     * Clear app cache every SUNDAY at 11 AM
     */
    @Scheduled(cron = "0 0 11 * * SUN")
    public void clearAppCache() {
        try {
            appCache.init();
            log.debug("App cache cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing app cache: ", e);
        }
    }
}
