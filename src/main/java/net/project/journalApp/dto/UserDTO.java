package net.project.journalApp.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@Schema(description = "Data Transfer Object for user registration and login.")
public class UserDTO {

    @Schema(description = "Unique username for the user.")
    @NotEmpty
    private String username;

    @Schema(description = "Email address of the user.")
    private String email;

    @Schema(description = "Password for the user account.")
    @NotEmpty
    private String password;

    @Schema(description = "Whether sentiment analysis is enabled for this user.")
    private boolean sentimentAnalysis;
}
