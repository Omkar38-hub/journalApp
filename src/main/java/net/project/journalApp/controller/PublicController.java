package net.project.journalApp.controller;

import lombok.extern.slf4j.Slf4j;
import net.project.journalApp.dto.JwtResponse;
import net.project.journalApp.dto.UserDTO;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.UserDetailsServiceImpl;
import net.project.journalApp.service.UserService;
import net.project.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/public")
@Slf4j
@Tag(name = "Public", description = "Public APIs for authentication and health check")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final PasswordEncoder passwordEncode = new BCryptPasswordEncoder();

    @GetMapping("/healthcheck")
    @Operation(summary = "Health check", description = "Returns OK if the service is running.")
    public String CheckHealth() {
        return "Ok";
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Registers a new user in the system.")
    public ResponseEntity<?> signUpUser(@RequestBody UserDTO user) {
        UserEntry newUser = new UserEntry();
        UserDetails userDetails = null;
        try {
            userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User creation failed: user already exists");
        } catch (Exception e) {
            newUser.setUsername(user.getUsername());
            newUser.setPassword(passwordEncode.encode(user.getPassword()));
            newUser.setSentimentAnalysis(user.isSentimentAnalysis());
            newUser.setRoles(Arrays.asList("USER"));
            boolean saved = userService.saveEntry(newUser);
            if (!saved) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User creation failed: Try Again...");
            }
            return new ResponseEntity<>("User Registered", HttpStatus.CREATED);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
    public ResponseEntity<?> loginUser(@RequestBody UserEntry user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            long expiresInSeconds = 60 * 60; // 1 hour
            String token = jwtUtil.generateToken(userDetails.getUsername(), expiresInSeconds);
            UserEntry dbUser = userService.findByUsername(user.getUsername());
            String role = (dbUser.getRoles() != null && !dbUser.getRoles().isEmpty())
                    ? dbUser.getRoles().get(0)
                    : "USER";

            JwtResponse response = new JwtResponse(token, expiresInSeconds, role);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
