package net.project.journalApp.controller;

import net.project.journalApp.entity.JournalEntry;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.JournalEntryService;
import net.project.journalApp.service.SentimentAnalysisService;
import net.project.journalApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal", description = "APIs for managing user journal entries")
public class JournalController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @GetMapping
    @Operation(summary = "Get all journal entries for user", description = "Returns all journal entries for the authenticated user.")
    public ResponseEntity<?> getAllUserEntry() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry user = userService.findByUsername(userName);
        List<JournalEntry> journalEntry = user.getJournalEntryList();
        if (!journalEntry.isEmpty()) {
            return new ResponseEntity<>(journalEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>("NO Journal Entry found for " + userName, HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @Operation(summary = "Create a new journal entry", description = "Creates a new journal entry for the authenticated user. Sentiment is analyzed automatically.")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userName = auth.getName();
            String sentiment = sentimentAnalysisService.analyzeSentiment(myEntry.getContent());
            myEntry.setSentiment(sentiment);
            myEntry.setDate(LocalDate.now());
            journalEntryService.saveEntry(myEntry, userName);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/id/{myId}")
    @Operation(summary = "Get journal entry by ID", description = "Returns a specific journal entry by its ID for the authenticated user.")
    public ResponseEntity<?> getMyEntry(@PathVariable String myId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry user = userService.findByUsername(userName);
        List<JournalEntry> collect = user.getJournalEntryList().stream().filter(x -> x.getId().equals(myId))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            JournalEntry journalEntry = journalEntryService.findById(myId).orElse(null);
            if (journalEntry != null) {
                return new ResponseEntity<>(journalEntry, HttpStatus.FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    @Operation(summary = "Delete journal entry by ID", description = "Deletes a specific journal entry by its ID for the authenticated user.")
    public ResponseEntity<?> deleteEntry(@PathVariable String myId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        boolean result = journalEntryService.deleteById(myId, userName);
        if (result) {
            return new ResponseEntity<>("Journal Entry has been deleted!", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/id/{myId}")
    @Operation(summary = "Update journal entry by ID", description = "Updates a specific journal entry by its ID for the authenticated user. Sentiment is re-analyzed.")
    public ResponseEntity<?> updateMyEntry(@PathVariable String myId, @RequestBody JournalEntry myEntry) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        UserEntry user = userService.findByUsername(userName);
        List<JournalEntry> collect = user.getJournalEntryList().stream().filter(x -> x.getId().equals(myId))
                .collect(Collectors.toList());
        if (!collect.isEmpty()) {
            JournalEntry old = journalEntryService.findById(myId).orElse(null);
            if (old != null) {
                old.setTitle(myEntry.getTitle() != null && !myEntry.getTitle().equals("") ? myEntry.getTitle()
                        : old.getTitle());
                old.setContent((myEntry.getContent() != null && !myEntry.getContent().equals("") ? myEntry.getContent()
                        : old.getContent()));
                String sentiment = sentimentAnalysisService.analyzeSentiment(old.getContent());
                old.setSentiment(sentiment);
                journalEntryService.saveEntry(old);
                return new ResponseEntity<>(old, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
