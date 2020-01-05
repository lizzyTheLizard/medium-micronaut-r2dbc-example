package medium.micronaut.r2dbc.example;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.TypeHint;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor(force=true)
@RequiredArgsConstructor
class Issue {
    private final UUID id;
    private final String name;
    private final String description;

    Issue partialUpdate(Issue partialIssue) {
        return new Issue(this.id,
                partialIssue.name != null ? partialIssue.name : this.name,
                partialIssue.description != null ? partialIssue.description : this.description);
    }
}
