package medium.micronaut.r2dbc.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Issue {
    private UUID id;
    private String name;
    private String description;

    Issue partialUpdate(Issue partialIssue) {
        if (partialIssue.getName() != null) {
            this.name = partialIssue.getName();
        }

        if (partialIssue.getDescription() != null) {
            this.description = partialIssue.getDescription();
        }
        return this;
    }
}
