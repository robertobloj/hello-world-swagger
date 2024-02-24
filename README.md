# Example app

The purpose of this app is to generate as strict swagger as possible.

Application uses [SpringDoc OpenAPI] to generate `OpenAPI specification`.

## Application Usage

### How to start the app ? 

```shell
gradlew bootRun
```

### How to generate `OpenAPI specification` during build:

```shell
gradlew clean generateOpenApiDocs
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

`OpenAPI specification` is available via [GUI] or as `json` via [OpenAPI description].
Alternatively you can download `yaml` file from [here].


[GUI]: http://localhost:8080/swagger-ui/index.html
[OpenAPI description]: http://localhost:8080/v3/api-docs
[here]: http://localhost:8080/v3/api-docs.yaml
[SpringDoc OpenAPI]: https://springdoc.org/#features