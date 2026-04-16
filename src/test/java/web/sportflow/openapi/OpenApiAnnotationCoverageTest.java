package web.sportflow.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OpenApiAnnotationCoverageTest {

  @Test
  void allOpenApiAnnotations_haveRuntimeRetentionAndTargets() {
    assertAnnotationContract(AdminForbiddenApiDoc.class, "403");
    assertAnnotationContract(BadRequestApiDoc.class, "400");
    assertAnnotationContract(ForbiddenApiDoc.class, "403");
    assertAnnotationContract(HtmlFragmentApiDoc.class, "200");
    assertAnnotationContract(HtmlRedirectApiDoc.class, "302");
    assertAnnotationContract(HtmlViewApiDoc.class, "200");
    assertAnnotationContract(InternalServerErrorApiDoc.class, "500");
    assertAnnotationContract(JsonSuccessApiDoc.class, "200");
    assertAnnotationContract(NotFoundApiDoc.class, "404");
    assertAnnotationContract(UnauthorizedApiDoc.class, "401");
  }

  private void assertAnnotationContract(
      Class<? extends Annotation> annotationClass, String expectedResponseCode) {
    Retention retention = annotationClass.getAnnotation(Retention.class);
    assertEquals(RetentionPolicy.RUNTIME, retention.value());

    Target target = annotationClass.getAnnotation(Target.class);
    Set<ElementType> elementTypes = Set.of(target.value());
    assertTrue(elementTypes.contains(ElementType.METHOD));
    assertTrue(elementTypes.contains(ElementType.TYPE));
    assertTrue(elementTypes.contains(ElementType.ANNOTATION_TYPE));

    ApiResponse apiResponse = annotationClass.getAnnotation(ApiResponse.class);
    assertEquals(expectedResponseCode, apiResponse.responseCode());
  }
}
