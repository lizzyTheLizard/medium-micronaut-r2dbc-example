package medium.micronaut.r2dbc.example;

import io.micronaut.http.annotation.Controller;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class Repository {
    private final Connection connection;

    public Repository(Connection connection) {
        this.connection = connection;
    }

    Mono<Issue> findById(UUID id) {
        final var statement = connection.createStatement("SELECT * FROM issue WHERE id=$1");
        statement.bind(0, id);
        return Mono.from(statement.execute())
                .map(result -> result.map(this::convertToIssue))
                .flatMap(Mono::from);
    }

    Flux<Issue> findAll() {
        final var statement = connection.createStatement("SELECT * FROM issue");
        return Mono.from(statement.execute())
                .map(result -> result.map(this::convertToIssue))
                .flatMapMany(Flux::from);
    }

    private Issue convertToIssue(Row r, RowMetadata rm) {
        final var id = (UUID) r.get("id");
        final var name = (String) r.get("name");
        final var description = (String) r.get("description");
        return new Issue(id, name, description);
    }

    Mono<Void> insert(Issue issue) {
        final var statement = connection.createStatement("INSERT INTO issue (id, name, description) VALUES($1, $2, $3)");
        statement.bind(0, issue.id);
        statement.bind(1, issue.name);
        statement.bind(2, issue.description);
        return Mono.from(statement.execute())
                .flatMap(this::checkOneRowUpdated);
    }

    Mono<Void> update(UUID id, Issue issue) {
        final var statement = connection.createStatement("UPDATE issue SET name=$2, description=$3 WHERE id=$1");
        statement.bind(0, id);
        statement.bind(1, issue.name);
        statement.bind(2, issue.description);
        return Mono.from(statement.execute())
                .flatMap(this::checkOneRowUpdated);
    }

    Mono<Void> deleteById(UUID id) {
        final var statement = connection.createStatement("DELETE FROM issue WHERE id=$1");
        statement.bind(0, id);
        return Mono.from(statement.execute())
                .flatMap(this::checkOneRowUpdated);
    }

    private Mono<Void> checkOneRowUpdated(Result result) {
        return Mono.from(result.getRowsUpdated())
                .flatMap(rows -> rows != 1 ? Mono.error(new RuntimeException("Issue not found")) : Mono.empty());
    }
}
