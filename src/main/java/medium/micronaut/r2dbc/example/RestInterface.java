package medium.micronaut.r2dbc.example;

import io.micronaut.http.annotation.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller("/issue")
@RequiredArgsConstructor
class RestInterface {
    private final Repository repository;

    @Get
    Flux<Issue> readAll() {
        return repository.findAll();
    }

    @Get("/{id}/")
    Mono<Issue> read(@PathVariable("id") UUID id) {
        return repository.findById(id);
    }

    @Post
    Mono<Issue> create(@Body Issue issue) {
        return repository.insert(issue)
                .then(repository.findById(issue.getId()));
    }

    @Put("/{id}")
    Mono<Issue> update(@PathVariable("id") UUID id, @Body Issue issue) {
        return repository.update(id, issue)
                .then(repository.findById(id));
    }

    @Patch("/{id}")
    Mono<Issue> partialUpdate(@PathVariable("id") UUID id, @Body Issue issue) {
        return repository.findById(id)
                .map(i -> i.partialUpdate(issue))
                .flatMap(i -> repository.update(id, i))
                .then(repository.findById(id));
    }

    @Delete("/{id}")
    Mono<Void> delete(@PathVariable("id") UUID id) {
        return repository.deleteById(id);
    }
}
