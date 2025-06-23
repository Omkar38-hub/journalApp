package net.project.journalApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class QuoteApiResponse {
    private Quote quote;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Quote {
        private String body;
        private String author;
    }
}
