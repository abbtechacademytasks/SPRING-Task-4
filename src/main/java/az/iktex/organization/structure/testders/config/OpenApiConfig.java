package az.iktex.organization.structure.testders.config;

import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.response.ErrorResponse;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI schoolApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("School API")
                        .version("1.0")
                        .description(
                                "Students, courses, departments, employees, authors, books and users"
                                        + errorCodeTable()
                        ));
    }

    @Bean
    public OpenApiCustomizer commonHeaders() {
        return openApi -> openApi.getPaths().values().forEach(path ->
                path.readOperations().forEach(operation -> {
                    operation.addParametersItem(
                            new HeaderParameter()
                                    .name("Accept-Language")
                                    .description("Response language: az/aze, en/eng, ru/rus. The lang query parameter takes priority.")
                                    .required(false)
                                    .schema(new StringSchema())
                    );

                    operation.addParametersItem(
                            new HeaderParameter()
                                    .name("X-Trace-Id")
                                    .description("Optional trace ID. If missing or invalid, the server generates a new one.")
                                    .required(false)
                                    .schema(new StringSchema()
                                            .pattern("^[A-Za-z0-9-]{8,64}$")
                                            .example("study-request-123"))
                    );
                })
        );
    }

    @Bean
    public OpenApiCustomizer commonErrorResponses() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }

            ModelConverters.getInstance()
                    .read(ErrorResponse.class)
                    .forEach(openApi.getComponents()::addSchemas);

            openApi.getPaths().values().forEach(path ->
                    path.readOperations().forEach(operation -> {
                        operation.getResponses()
                                .addApiResponse("400", errorResponse("Bad Request", ErrorCode.VALIDATION_ERROR))
                                .addApiResponse("404", errorResponse("Not Found", ErrorCode.URL_NOT_FOUND))
                                .addApiResponse("409", errorResponse("Conflict", ErrorCode.DATA_INTEGRITY_VIOLATION))
                                .addApiResponse("500", errorResponse("Internal Server Error", ErrorCode.INTERNAL_ERROR));
                    })
            );
        };
    }

    private ApiResponse errorResponse(String description, ErrorCode errorCode) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json",
                                new MediaType().schema(
                                        new Schema<>()
                                                .$ref("#/components/schemas/ErrorResponse")
                                ).example(Map.of(
                                        "status", errorCode.getStatus().value(),
                                        "code", errorCode.getCode(),
                                        "message", description,
                                        "errors", Map.of(),
                                        "traceId", "study-request-123",
                                        "timestamp", "2026-11-09T18:30:00"
                                ))
                        )
                );
    }

    private String errorCodeTable() {
        StringBuilder table = new StringBuilder(
                "\n\n## Error codes\n\n"
                        + "| Code | HTTP status | Message key |\n"
                        + "| --- | --- | --- |\n"
        );

        for (ErrorCode error : ErrorCode.values()) {
            table.append("| ")
                    .append(error.getCode())
                    .append(" | ")
                    .append(error.getStatus().value())
                    .append(" | ")
                    .append(error.getMessageKey())
                    .append(" |\n");
        }

        return table.toString();
    }
}
