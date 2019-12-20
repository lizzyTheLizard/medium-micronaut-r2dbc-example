package medium.micronaut.r2dbc.example;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Issue {
    final UUID id;
    final String name;
    final String description;

    Issue(@JsonProperty("id") UUID id, @JsonProperty("name") String name,
          @JsonProperty("description") String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    Issue partialUpdate(Issue partialIssue) {
        return new Issue(this.id,
                partialIssue.name != null ? partialIssue.name : this.name,
                partialIssue.description != null ? partialIssue.description : this.description);
    }
}
