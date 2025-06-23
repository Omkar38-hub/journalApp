package net.project.journalApp.controller;

import net.project.journalApp.dto.WeatherResponse;
import net.project.journalApp.dto.QuoteResponse;
import net.project.journalApp.entity.EmailDetails;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.EmailServiceImpl;
import net.project.journalApp.service.QuoteService;
import net.project.journalApp.service.UserService;
import net.project.journalApp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "APIs for user profile and preferences")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private QuoteService quoteService;

    private static final PasswordEncoder passwordEncode = new BCryptPasswordEncoder();

    @PutMapping
    @Operation(summary = "Update user profile", description = "Updates the authenticated user's profile information.")
    public ResponseEntity<?> updateUserEntry(@RequestBody UserEntry user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry userInDb = userService.findByUsername(userName);
        userInDb.setEmail(user.getEmail());
        userInDb.setSentimentAnalysis(user.isSentimentAnalysis());
        userService.saveEntry(userInDb);
        return new ResponseEntity<>(userInDb, HttpStatus.OK);
    }

    @PutMapping("/password")
    @Operation(summary = "Change password", description = "Changes the authenticated user's password.")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body) {
        try {
            String currPassword = body.get("currentPassword");
            String newPassword = body.get("password");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password cannot be empty.");
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userName = auth.getName();
            UserEntry userInDb = userService.findByUsername(userName);
            String password = userInDb.getPassword();
            if (!passwordEncode.matches(currPassword, password)) {
                return ResponseEntity.badRequest().body("Current Password is incorrect.");
            }
            userInDb.setPassword(passwordEncode.encode(newPassword));
            boolean updated = userService.saveEntry(userInDb);
            if (updated) {
                return ResponseEntity.ok("Password updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update password.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password.");
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete user", description = "Deletes the authenticated user's account.")
    public ResponseEntity<?> deleteUserEntry() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry user = userService.findByUsername(userName);
        Boolean deleted = userService.deleteById(user.getId());
        if (deleted) {
            return new ResponseEntity<>("User Deleted Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to delete User", HttpStatus.EXPECTATION_FAILED);
    }

    @GetMapping
    @Operation(summary = "Get user profile", description = "Returns the authenticated user's profile information.")
    public ResponseEntity<?> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry userInDb = userService.findByUsername(userName);
        return new ResponseEntity<>(userInDb, HttpStatus.OK);
    }

    @GetMapping("/daily-quote")
    @Operation(summary = "Get daily quote", description = "Returns the quote of the day.")
    public ResponseEntity<QuoteResponse> getDailyQuote() {
        QuoteResponse quote = quoteService.getQuoteOfTheDay();
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/weather")
    @Operation(summary = "Get weather and send email", description = "Returns weather info and sends an email to the user.")
    public ResponseEntity<?> quoteForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        WeatherResponse response = weatherService.getWeather("New_Delhi");
        String greetings = "";
        if (response != null) {
            greetings = ", The Temperature of New_Delhi is " + response.getCurrent().getTemperature();
        }
        String message = "Hello! " + userName + greetings;
        String status = emailService.sendSimpleMail(EmailDetails.builder().msgBody(message).subject("Today's Weather")
                .recipient("abhiomkar7@gmail.com").build());
        return new ResponseEntity<>(message + " " + status, HttpStatus.OK);
    }
}
