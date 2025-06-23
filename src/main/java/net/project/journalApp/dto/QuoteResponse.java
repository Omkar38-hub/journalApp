package net.project.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing a quote and its author.")
public class QuoteResponse {
    @Schema(description = "The quote text.")
    private String quote;
    @Schema(description = "The author of the quote.")
    private String author;
}
