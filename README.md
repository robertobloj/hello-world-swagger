# Example app

The purpose of this app is to generate as strict swagger as possible.

Application uses [SpringDoc OpenAPI] to generate `OpenAPI specification`.


## Application Usage

### How to start the app ? 
ww
```shell
gradlew bootRun
```

### How to generate `OpenAPI specification` during build?

```shell
gradlew clean generateOpenApiDocs
```

Now you can check [OpenApi specification] written to build directory. 

### OpenAPI Specification

`OpenAPI specification` is available via [GUI] or as `json` via [OpenAPI description].
Alternatively you can download `yaml` file from [here].


[GUI]: http://localhost:8080/swagger-ui/index.html
[OpenAPI description]: http://localhost:8080/v3/api-docs
[here]: http://localhost:8080/v3/api-docs.yaml
[SpringDoc OpenAPI]: https://springdoc.org/
[OpenApi specification]: build/openapi.json