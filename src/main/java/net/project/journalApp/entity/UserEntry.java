package net.project.journalApp.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;

@Document(collection = "users") // To achive OR(connect with DB)
@Data // Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString
      // @EqualsAndHashCode.
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a user in the system.")
public class UserEntry {

    @Schema(description = "Unique identifier of the user.")
    @Id
    private String id;
    @NonNull
    @Indexed(unique = true)
    @Schema(description = "Unique username of the user.")
    private String username;

    @Schema(description = "Email address of the user.")
    private String email;
    @Schema(description = "Whether sentiment analysis is enabled for this user.")
    private boolean sentimentAnalysis;
    @NonNull
    @Schema(description = "Hashed password of the user.")
    private String password;
    @Schema(description = "Roles assigned to the user (e.g., USER, ADMIN).")
    private List<String> roles;
    @DBRef
    @Schema(description = "List of journal entries associated with the user.")
    private List<JournalEntry> journalEntryList = new ArrayList<>();

    @Builder
    public UserEntry(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }
}
