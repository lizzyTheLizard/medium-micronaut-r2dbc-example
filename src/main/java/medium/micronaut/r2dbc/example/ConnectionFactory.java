package medium.micronaut.r2dbc.example;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import reactor.core.publisher.Mono;

@Factory
public class ConnectionFactory {
    private final io.r2dbc.spi.ConnectionFactory factory;

    public ConnectionFactory() {
        final var baseOptions = ConnectionFactoryOptions.parse("r2dbc:postgresql://postgres:postgres@postgres");
        final var options = ConnectionFactoryOptions.builder().from(baseOptions).build();
        this.factory = ConnectionFactories.get(options);
    }

    @Bean
    public Connection connection() {
        return Mono.from(factory.create()).block();
    }
}
