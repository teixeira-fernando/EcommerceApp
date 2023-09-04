![](https://github.com/teixeira-fernando/EcommerceApp/workflows/JavaCIMaven/badge.svg)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=coverage)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=bugs)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=teixeira-fernando_EcommerceApp&metric=sqale_index)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)

[![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=teixeira-fernando_EcommerceApp)](https://sonarcloud.io/dashboard?id=teixeira-fernando_EcommerceApp)

# E-commerce App - Exploring QA strategies for microservices with Synchronous and Asynchronous communication 

The initial objective of this project was to further study the differences in the development and implementation of quality strategies for microservices with synchronous and asynchronous communication. I decided to do all the development of the application and the tests from scratch to be able to better observe some details in practice.

## Microservices

* Shop
* Inventory
* Shipment

![alt text](images/EcommerceappDiagram.png "EcommerceApp modules comunication")

## Instructions to run the project

The docker-compose file contains the pre-requisites to run the application: MongoDB and Kafka

It's also neccessary to generate some depencies jars from the modules:

```
mvn clean install
```

Now, you can bring up the docker-compose: 

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

* Unit Tests: <b>Junit5 and Mockito</b>
* Integration tests: <b>Spring Boot Test, WireMock and EmbeddedKafka</b> (when testing asynchronous events)
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

This project contains contract tests using Pact. You need to setup your [PactFlow](https://pactflow.io/) account in order to use it.


PS: Be aware of some environment variables you may need to run some of the commands below and also the pipeline:

* PACT_BROKER_URL
  * Correspond to the url of your personal pact flow account
* PACT_BROKER_TOKEN
  * Correspond to the token from your pact flow account with full rights to read and write
* PACT_BROKER_HOST
  * Correspond to the host value of your Pact flow account

### Running the pact tests locally

Besides the CI configuration where the tests are configured to run. You can also run them locally. You need first to also set some of the variables mentioned above.

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

# Articles with more info related to this project

https://medium.com/assertqualityassurance/creating-a-test-strategy-for-asynchronous-microservices-applications-1397f7755e85
