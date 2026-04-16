package web.sportflow.openapi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "200",
    description = "Vue HTML retournee avec succes",
    content =
        @Content(
            mediaType = "text/html",
            examples = @ExampleObject(value = "<html><body><h1>Vue HTML</h1></body></html>")))
public @interface HtmlViewApiDoc {}
