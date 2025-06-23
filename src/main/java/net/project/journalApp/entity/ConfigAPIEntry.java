package net.project.journalApp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import io.swagger.v3.oas.annotations.media.Schema;

@Document(collection = "external_api") // To achive OR(connect with DB)
@Data // Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString
      // @EqualsAndHashCode.
@NoArgsConstructor
@Schema(description = "Represents a configuration entry for an external API.")
public class ConfigAPIEntry {

    @Schema(description = "Configuration key.")
    private String key;
    @Schema(description = "Configuration value.")
    private String value;
}
