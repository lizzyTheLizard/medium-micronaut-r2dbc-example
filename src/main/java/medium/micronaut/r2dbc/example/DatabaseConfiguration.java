package medium.micronaut.r2dbc.example;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;

@Factory
class DatabaseConfiguration {

    @Bean
    ConnectionPool connectionPool() {
        final ConnectionFactoryOptions baseOptions = ConnectionFactoryOptions.parse("r2dbc:postgresql://postgres:postgres@postgres");
        final ConnectionFactoryOptions options = ConnectionFactoryOptions.builder().from(baseOptions).build();
        final ConnectionFactory factory = ConnectionFactories.get(options);
        final ConnectionPoolConfiguration poolOptions = ConnectionPoolConfiguration.builder(factory).build();
        return new ConnectionPool(poolOptions);
    }
}
