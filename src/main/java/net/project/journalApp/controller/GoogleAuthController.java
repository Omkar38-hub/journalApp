package net.project.journalApp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.service.UserDetailsServiceImpl;
import net.project.journalApp.service.UserService;
import net.project.journalApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
@Slf4j
@Tag(name = "Authentication Controller", description = "Handles Google OAuth2 and JWT login.")
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${app.cors.allowed-origin}")
    private String allowedOrigin;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/callback")
    @Operation(
            summary = "Handle Google OAuth2 callback",
            description = "Exchanges Google auth code for token, creates or retrieves user, and returns JWT."
    )
    public ResponseEntity<?> handleCallbackUrl(@RequestParam String code) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Exchange authcode for token
            String tokenEndPoint = "https://oauth2.googleapis.com/token";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndPoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(email);
                } catch (Exception e) {
                    UserEntry user = new UserEntry();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));
                    userService.saveEntry(user);
                }
                long expiresInSeconds = 60 * 60; //1 hr
                String token = jwtUtil.generateToken(email,expiresInSeconds);

                UserEntry dbUser = userService.findByUsername(email);
                String role = (dbUser.getRoles() != null && !dbUser.getRoles().isEmpty())
                        ? dbUser.getRoles().get(0)
                        : "USER";

                // Redirect to frontend with token
                String frontendUrl = allowedOrigin + "/auth/callback?token=" + token
                        + "&role=" + role + "&expiresIn=" + expiresInSeconds;
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.LOCATION, frontendUrl);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Exception occurred while handleGoogleCallback ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
