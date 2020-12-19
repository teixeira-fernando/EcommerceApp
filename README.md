![](https://github.com/teixeira-fernando/EcommerceApp/workflows/JavaCIMaven/badge.svg)

# E-commerce App - Microservices Java

## Microservices

* Shop
* Inventory
* Shipment

## Instructions

Run:

```
mvn spring-boot:run (in the sub-module directory)
```

Generate Jar:

``` 
mvn clean install 
```

Check code Style:

``` 
mvn spotless::check (in the sub-module directory)
```

Apply code Style:

``` 
mvn spotless::apply (in the sub-module directory)
```

## Instructions for contract tests wth Pact

This project contains contract tests using Pact. In order to run it, first of all you need to bring up the pact broker.
To run the pact-broker using Docker, execute:

``` 
docker-compose up -d
```

Then you can go to the producer module and execute the contract tests, to generate the contracts. The pact files will be
written in the target/pact folder. After that, you can publish the pact into the pact broker with the following command:

``` 
mvn pact:publish (in the shop module)
```

Then you can run the contract tests in the provider side, where it is already configured to get it from the pact broker

``` 
mvn -Dtest=**/contract/** test
``` 

## Utils

* Install and activate Intellij plugin to format on IDE:
    * https://plugins.jetbrains.com/plugin/8527-google-java-format

* There is a swagger documentation configured for each module. After running the desired module, you can navigate
  to: http://localhost:{port}/swagger-ui/