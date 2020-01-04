package medium.micronaut.r2dbc.example;

import io.micronaut.http.annotation.Controller;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
class Repository {
    private final ConnectionPool connectionPool;

    Mono<Issue> findById(UUID id) {
        return connectionPool.create().flatMap(connection -> {
                    final Statement statement = connection.createStatement("SELECT * FROM issue WHERE id=$1");
                    statement.bind(0, id);
                    return Mono.from(statement.execute())
                            .map(result -> result.map(this::convertToIssue))
                            .flatMap(Mono::from)
                            .timeout(Duration.ofSeconds(1))
                            .doOnTerminate(() -> Mono.from(connection.close()).subscribe());
                });
    }

    Flux<Issue> findAll() {
        return connectionPool.create().flatMapMany(connection -> {
            final Statement statement = connection.createStatement("SELECT * FROM issue");
            return Mono.from(statement.execute())
                    .map(result -> result.map(this::convertToIssue))
                    .flatMapMany(Flux::from)
                    .timeout(Duration.ofSeconds(1))
                    .doOnTerminate(() -> Mono.from(connection.close()).subscribe());
        });
    }

    private Issue convertToIssue(Row r, RowMetadata rm) {
        final UUID id = (UUID) r.get("id");
        final String name = (String) r.get("name");
        final String description = (String) r.get("description");
        return new Issue(id, name, description);
    }

    Mono<Void> insert(Issue issue) {
        return connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement("INSERT INTO issue (id, name, description) VALUES($1, $2, $3)");
            statement.bind(0, issue.getId());
            statement.bind(1, issue.getName());
            statement.bind(2, issue.getDescription());
            return Mono.from(statement.execute())
                    .flatMap(this::checkOneRowUpdated)
                    .timeout(Duration.ofSeconds(1))
                    .doOnTerminate(() -> Mono.from(connection.close()).subscribe());
        });
    }

    Mono<Void> update(UUID id, Issue issue) {
        return connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement("UPDATE issue SET name=$2, description=$3 WHERE id=$1");
            statement.bind(0, id);
            statement.bind(1, issue.getName());
            statement.bind(2, issue.getDescription());
            return Mono.from(statement.execute())
                    .flatMap(this::checkOneRowUpdated)
                    .timeout(Duration.ofSeconds(1))
                    .doOnTerminate(() -> Mono.from(connection.close()).subscribe());
        });
    }

    Mono<Void> deleteById(UUID id) {
        return connectionPool.create().flatMap(connection -> {
            final Statement statement = connection.createStatement("DELETE FROM issue WHERE id=$1");
            statement.bind(0, id);
            return Mono.from(statement.execute())
                    .flatMap(this::checkOneRowUpdated)
                    .timeout(Duration.ofSeconds(1))
                    .doOnTerminate(() -> Mono.from(connection.close()).subscribe());
        });
    }

    private Mono<Void> checkOneRowUpdated(Result result) {
        return Mono.from(result.getRowsUpdated())
                .flatMap(rows -> rows != 1 ? Mono.error(new RuntimeException("Issue not found")) : Mono.empty());
    }
}
