package net.project.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "journal_entries") // To achive OR(connect with DB)
@Data // Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString
      // @EqualsAndHashCode.
@NoArgsConstructor
@Schema(description = "Represents a journal entry created by a user.")
public class JournalEntry {

    @Schema(description = "Unique identifier of the journal entry.")
    @Id
    private String id;
    @Schema(description = "Title of the journal entry.")
    @NonNull
    private String title;
    @Schema(description = "Content/body of the journal entry.")
    private String content;
    @Schema(description = "Date when the journal entry was created.")
    private LocalDate date;
    @Schema(description = "Sentiment analysis result for the journal entry content.")
    private String sentiment;
}
