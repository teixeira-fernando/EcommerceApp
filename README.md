![](https://github.com/teixeira-fernando/EcommerceApp/workflows/JavaCIMaven/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=coverage)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=bugs)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=sqale_index)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=teixeira-fernando_EcommerceApp)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)

# E-commerce App - Microservices Java

## Microservices

* Shop
* Inventory
* Shipment

![alt text](images/EcommerceappDiagram.png "EcommerceApp modules comunication")

## Instructions to run the project

The docker-compose file contains the pre-requisites to run the application: MongoDB and Kafka

So, first run: 

```
docker-compose up -d
```

Then, you can run the application modules:

Run Inventory:

```
mvn -f inventory/ spring-boot:run 
```

Run Shop:

```
mvn -f shop/ spring-boot:run 
```

Run Shipment:

```
mvn -f shipment/ spring-boot:run 
```

## QA Strategy

* Unit Tests: <b>Junit5</b>
* Integration tests: <b>Spring Boot Test and EmbeddedKafka</b> (when testing asynchronous events)
* Quality Metrics:
  * Mutation Tests/Mutation Coverage: <b>PITest</b>
  * Code Coverage: <b>Jacoco</b>
  * Technical Debt, Code Smells and other complementary metrics : <b>Sonar Cloud</b>
* Contract tests: <b>Pact framework</b>
* Continuous Integration: This project uses Github Action for Continuous Integration, where it executes all the tests and Sonar Cloud Analysis for every pull request, making easier the process of integration of every new code, also facilitating the process of Code Review.

## Development technology stack

* Development:
  * Spring Boot 
  * Java
  * Maven
* Kafka
* MongoDB
* Github Actions
* Docker

## Instructions for contract tests with Pact

This project contains contract tests using Pact. In order to run it, first you need to bring up the pact broker.
To run the pact-broker using Docker, execute:

``` 
docker-compose -f pactbroker_dockercompose/docker-compose.yml up -d
```

Then you can go to the producer module and execute the contract tests, to generate the contracts. The pact files will be
written in the target/pact folder. It is configured to automatically publish the pacts into the pact broker.

``` 
mvn -f {folder}/ -Dtest=**/contract/*ConsumerPact test
``` 

If you want to manually publish the pacts into the pact broker, you can execute the following command:

``` 
mvn pact:publish 
```

Then you can run the contract tests in the provider side, where it is already configured to get it from the pact broker

``` 
mvn -f {folder}/ -Dtest=**/contract/*ProviderPact test
``` 

## Other info and Utilities

* Install and activate Intellij plugin to format on IDE:
    * https://plugins.jetbrains.com/plugin/8527-google-java-format

* There is a swagger documentation configured for each module. After running the desired module, you can navigate
  to: http://localhost:{port}/swagger-ui/
  

* Check code Style:

``` 
mvn spotless::check (in the sub-module directory)
```

* Apply code Style:

``` 
mvn spotless::apply (in the sub-module directory)
```