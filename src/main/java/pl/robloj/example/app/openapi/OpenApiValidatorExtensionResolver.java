package pl.robloj.example.app.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.pl.PESEL;
import org.springframework.stereotype.Component;
import pl.robloj.example.app.dto.validators.IBAN;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Component
public class OpenApiValidatorExtensionResolver extends ModelResolver {

    private final List<Class<? extends Annotation>> extendedInfo = Arrays.asList(
            Email.class, URL.class, PESEL.class, PastOrPresent.class, Past.class,
            FutureOrPresent.class, Future.class, IBAN.class
        );

    private final Class<?>[] handledValidations = {
            NotBlank.class,
            NotEmpty.class,
            NotNull.class,
            Min.class,
            Max.class,
            DecimalMin.class,
            DecimalMax.class,
            Pattern.class,
            Size.class
        };

    private final Package[] allowedPackages = {
            NotNull.class.getPackage(),     // package contains all jakarta validations
            URL.class.getPackage(),         // extra hibernate validations
            PESEL.class.getPackage(),       // extra hibernate validations for Poland
            IBAN.class.getPackage()         // custom validations for project
        };

    public OpenApiValidatorExtensionResolver(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected void applyBeanValidatorAnnotations(
            Schema property, Annotation[] annotations, Schema parent, boolean applyNotNull) {

        super.applyBeanValidatorAnnotations(property, annotations, parent, applyNotNull);
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                boolean handled = false;
                for (Class<?> check : handledValidations) {
                    if (annotationType == check) {
                        handled = true;
                        break;
                    }
                }
                createExtensionInfo(property, annotation, handled, annotationType);
            }
        }
    }

    private void createExtensionInfo(Schema<?> property, Annotation annotation, boolean handled, Class<? extends Annotation> annotationType) {
        if (!handled) {
            Package annotationPackage = annotationType.getPackage();
            boolean allowed = false;
            for (Package allowedPackage : allowedPackages) {
                if (allowedPackage == annotationPackage) {
                    allowed = true;
                    break;
                }
            }
            if (allowed) {
                Map<String, Object> extensions = property.getExtensions();
                String extensionKey = "x-validation-" + annotationType.getSimpleName().toLowerCase();
                if (!(extensions != null && extensions.containsKey(extensionKey))) {
                    Object value = describeAnnotation(annotation, annotationType);
                    property.addExtension(extensionKey, value);
                }
            }
        }
    }

    private Object describeAnnotation(Annotation annotation, Class<? extends Annotation> annotationType) {
        if (extendedInfo.contains(annotationType)) {
            return new ExtensionValidationInfo(
                    annotationType,
                    "FIXME define validator?",
                    "some description"
                );
        }
        //just marker, nothing more
        return true;
    }
}
