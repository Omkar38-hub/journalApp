package net.project.journalApp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Details required to send an email.")
public class EmailDetails {
    @Schema(description = "Recipient email address.")
    private String recipient;
    @Schema(description = "Body of the email message.")
    private String msgBody;
    @Schema(description = "Subject of the email.")
    private String subject;
    @Schema(description = "Sender email address.")
    private String sender;
}
