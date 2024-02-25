package pl.robloj.example.app.openapi;

import java.lang.annotation.Annotation;

public record ExtensionValidationInfo(Class<? extends Annotation> annotation, String validator, String description) {
}
