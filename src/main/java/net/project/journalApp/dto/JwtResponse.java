package net.project.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String access_token;
    private String token_type = "Bearer";
    private int not_before_policy = 0;
    private String session_state = UUID.randomUUID().toString(); // can be fixed or dynamic
    private String scope = "profile email groupmembership";
    private long expires_in;

    private String role;

    public JwtResponse(String access_token, long expires_in,String role) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.role=role;
    }
}

