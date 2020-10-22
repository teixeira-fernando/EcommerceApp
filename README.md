![](https://github.com/teixeira-fernando/EcommerceApp/workflows/JavaCIMaven/badge.svg)

# E-commerce App - Microservices Java

## Microservices
* Shop
* Inventory

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

Utils: 

Install and activate Intellij plugin to format on IDE:
* https://plugins.jetbrains.com/plugin/8527-google-java-format