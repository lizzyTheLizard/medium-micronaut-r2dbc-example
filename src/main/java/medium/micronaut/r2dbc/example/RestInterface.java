package medium.micronaut.r2dbc.example;

import io.micronaut.http.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.inject.Inject;
import java.util.UUID;

@Controller("/issue")
public class RestInterface {
    @Inject
    private Repository repository;

    @Get("/")
    public Flux<Issue> readAll() {
        return repository.findAll();
    }

    @Get("/{id}/")
    public Mono<Issue> read(@PathVariable("id") UUID id) {
        return repository.findById(id);
    }

    @Post("/")
    public Mono<Issue> create(@Body Issue issue) {
        return repository.insert(issue)
                .then(repository.findById(issue.id));
    }

    @Put("/{id}")
    public Mono<Issue> update(@PathVariable("id") UUID id, @Body Issue issue) {
        return repository.update(id, issue)
                .then(repository.findById(id));
    }

    @Patch("/{id}")
    public Mono<Issue> partialUpdate(@PathVariable("id") UUID id, @Body Issue issue) {
        return repository.findById(id)
                .map(i -> i.partialUpdate(issue))
                .flatMap(i -> repository.update(id, i))
                .then(repository.findById(id));
    }

    @Delete("/{id}")
    public Mono<Void> delete(@PathVariable("id") UUID id) {
        return repository.deleteById(id);
    }
}
