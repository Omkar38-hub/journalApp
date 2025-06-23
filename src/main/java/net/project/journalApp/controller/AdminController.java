package net.project.journalApp.controller;

import net.project.journalApp.cache.AppCache;
import net.project.journalApp.entity.EmailDetails;
import net.project.journalApp.entity.JournalEntry;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.EmailServiceImpl;
import net.project.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin operations for user and journal management")
public class AdminController {
    @Autowired
    private UserService userService;

    @Autowired
    private AppCache appCache;

    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("all-users")
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system.")
    public ResponseEntity<?> getAllUsers() {
        List<UserEntry> users = userService.getAll();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>("NO User found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("all-admins")
    @Operation(summary = "Get all admins", description = "Returns a list of all admin users.")
    public ResponseEntity<?> getAllAdmins() {
        List<UserEntry> users = userService.getAll();
        if (users != null && !users.isEmpty()) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
        return new ResponseEntity<>("NO User found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    @Operation(summary = "Delete user by ID", description = "Deletes a user by their unique ID.")
    public ResponseEntity<?> deleteEntry(@PathVariable String myId) {
        Boolean deleted = userService.deleteById(myId);
        if (deleted) {
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to delete User", HttpStatus.EXPECTATION_FAILED);
    }

    @PutMapping("/change-role")
    @Operation(summary = "Change user role", description = "Updates the roles of a user. Only admins can perform this action.")
    public ResponseEntity<?> changeRole(@RequestBody Map<String, Object> body) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminName = auth.getName();
            UserEntry admin = userService.findByUsername(adminName);
            String username = (String) body.get("username");
            String email = (String) body.get("email");
            List<String> roles = (List<String>) body.get("roles");

            if (username == null || roles == null || roles.isEmpty() || email == null) {
                return ResponseEntity.badRequest().body("Username, roles and Email must be provided.");
            }
            // You can fetch the user and update roles here
            UserEntry user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            String dbEmail = user.getEmail();
            if (dbEmail == null) {
                user.setEmail(email);
            } else if (!email.equals(dbEmail)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EMail ID does not match");
            }
            user.setRoles(roles); // Ensure your entity supports setting roles as List<String>
            userService.saveAdmin(user);
            String message = String.format(
                    "Hello %s,\n\n" +
                            "Congratulations! Your role(s) have been successfully updated to: %s.\n\n" +
                            "Best regards,\n%s",
                    user.getUsername(), // User's name
                    String.join(", ", user.getRoles()), // Roles list formatted as "ADMIN, USER"
                    adminName // Admin name from authentication
            );
            String status = emailService.sendSimpleMail(
                    EmailDetails.builder().msgBody(message).subject("Congratulations !! 🥳,Role change alert")
                            .recipient(email).sender(admin.getEmail()).build());
            return new ResponseEntity<>("User roles updated successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user roles.");
        }
    }

    @GetMapping("clear-app-cache")
    @Operation(summary = "Clear application cache", description = "Clears the weather cache for the application.")
    public ResponseEntity<?> clearCache() {
        appCache.init();
        return new ResponseEntity<>("Weather Cache Updated", HttpStatus.OK);
    }

    @GetMapping("weekly-mood-stats")
    @Operation(summary = "Get weekly mood stats", description = "Returns sentiment statistics for the last 7 days across all users.")
    public ResponseEntity<?> getWeeklyMoodStats() {
        try {
            List<UserEntry> users = userService.getAll();
            Map<String, Map<String, Integer>> weeklyStats = new LinkedHashMap<>();

            // Initialize the last 7 days
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                String dateKey = date.format(formatter);
                weeklyStats.put(dateKey, new HashMap<>());

                // Initialize sentiment counts for this day
                weeklyStats.get(dateKey).put("HAPPY", 0);
                weeklyStats.get(dateKey).put("PLEASANT", 0);
                weeklyStats.get(dateKey).put("CALM", 0);
                weeklyStats.get(dateKey).put("NEUTRAL", 0);
                weeklyStats.get(dateKey).put("SAD", 0);
                weeklyStats.get(dateKey).put("ANGRY", 0);
            }

            // Count sentiments for each day
            for (UserEntry user : users) {
                if (user.getJournalEntryList() != null) {
                    for (JournalEntry entry : user.getJournalEntryList()) {
                        LocalDate entryDate = entry.getDate();
                        if (entryDate != null && entryDate.isAfter(today.minusDays(7))) {
                            String dateKey = entryDate.format(formatter);
                            String sentiment = entry.getSentiment();

                            if (sentiment != null && weeklyStats.containsKey(dateKey)) {
                                String upperSentiment = sentiment.toUpperCase();
                                weeklyStats.get(dateKey).put(upperSentiment,
                                        weeklyStats.get(dateKey).getOrDefault(upperSentiment, 0) + 1);
                            }
                        }
                    }
                }
            }

            // Convert to the format expected by the frontend
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map.Entry<String, Map<String, Integer>> entry : weeklyStats.entrySet()) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", entry.getKey());
                dayData.put("sentiments", entry.getValue());
                result.add(dayData);
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error fetching weekly mood statistics", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
