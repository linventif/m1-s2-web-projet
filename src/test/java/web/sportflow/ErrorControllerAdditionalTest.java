package web.sportflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class ErrorControllerAdditionalTest {

  @Test
  void handleNoResource_and_handleException_coverBranches() {
    ErrorController controller = new ErrorController();

    Model model = new ExtendedModelMap();
    MockHttpServletResponse response = new MockHttpServletResponse();

    NoResourceFoundException notFound =
        new NoResourceFoundException(HttpMethod.GET, "/missing/path", "missing");
    String notFoundView = controller.handleNoResource(notFound, model, response);

    assertEquals("error", notFoundView);
    assertEquals(404, response.getStatus());

    Model genericModel = new ExtendedModelMap();
    MockHttpServletResponse genericResponse = new MockHttpServletResponse();
    String errorView =
        controller.handleException(
            new IllegalStateException("boom"), genericModel, genericResponse);
    assertEquals("error", errorView);
    assertEquals(500, genericResponse.getStatus());

    Model nullThrowableModel = new ExtendedModelMap();
    MockHttpServletResponse nullThrowableResponse = new MockHttpServletResponse();
    controller.handleException(null, nullThrowableModel, nullThrowableResponse);
    assertEquals("Erreur inconnue", nullThrowableModel.getAttribute("message"));
  }
}
