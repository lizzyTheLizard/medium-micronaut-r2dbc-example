# medium-micronaut-r2dbc-example

Simple example with Postgres, R2DBC and Micronaut. Provides a Rest-Interfaces that is
reactive connected to a database table.

### Prerequisites

[Docker Compose](https://docs.docker.com/compose/) needs to be installed to run this example

## Run the example

You can run this example with
```
./gradlew assemble && docker-compose up --force-recreate --build && docker-compose rm -fsv
```
The rest interface can then be reached under http://localhost:8080/issue
## Built With

* [Micronaut](https://micronaut.io/) - Java Framework Used
* [R2DBC](https://r2dbc.io) - Reactive Database API for Java

## Authors

See [contributors](https://github.com/lizzyTheLizard/medium-micronaut-r2dbc-example/graphs/contributors) for a list of contibutors

## License

This project is licensed under the Apache License - see the [LICENSE](LICENSE) file for details
