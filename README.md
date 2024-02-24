# Example app

The purpose of this app is to generate as strict swagger as possible.

## Application Usage

### How to start the app ? 

```shell
gradlew bootRun
```

### Example endpoints

Go to the browser or postman and run:

```shell
http://localhost:8080/greeting
```

or

```shell
http://localhost:8080/greeting?name=Robert
```

### OpenAPI Specification

OpenAPI specification is available via [GUI] or via [OpenAPI description].

[GUI]: http://localhost:8080/swagger-ui/index.html
[OpenAPI description]: http://localhost:8080/v3/api-docs 
