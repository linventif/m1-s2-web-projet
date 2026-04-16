package web.sportflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import web.sportflow.user.RegistrationDTO;
import web.sportflow.user.UserService;
import web.sportflow.weather.TestController;
import web.sportflow.weather.WeatherStatsDTO;

class BasicControllerCoverageTest {

  @Test
  void homeController_redirectsToExpectedRoutes() {
    HomeController controller = new HomeController();

    assertEquals("redirect:/dashboard", controller.redirectToDashboardHome());
    assertEquals("redirect:/user/dashboard", controller.redirectToDashboard());
    assertEquals("redirect:/dashboard", controller.redirectTypoDashbord());
  }

  @Test
  void authController_routesAndRegistration_callUserService() {
    UserService userService = org.mockito.Mockito.mock(UserService.class);
    AuthController controller = new AuthController(userService);
    Model model = new ExtendedModelMap();

    assertEquals("register", controller.showRegistrationForm(model));
    assertEquals("login", controller.login());

    RegistrationDTO dto =
        new RegistrationDTO("alice@example.com", "secret", "Alice", "Martin", null, 55.0, 165.0);
    assertEquals("redirect:/login?success", controller.registerUser(dto));
    verify(userService).registerUser(dto);
  }

  @Test
  void testWeatherController_populatesModel() {
    TestController controller = new TestController();
    Model model = new ExtendedModelMap();

    assertEquals("test-page", controller.testWeather(model));
    Object weatherData = model.getAttribute("weatherData");
    assertEquals(WeatherStatsDTO.class, weatherData.getClass());
  }

  @Test
  void errorController_handlers_populateErrorModel() {
    ErrorController controller = new ErrorController();
    Model model = new ExtendedModelMap();
    HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
    Throwable throwable = new IllegalStateException("boom");

    assertEquals("error", controller.handleException(throwable, model, response));
    verify(response).setStatus(500);
    assertEquals(500, model.getAttribute("httpStatus"));
    assertEquals("Internal Server Error", model.getAttribute("reason"));
    assertEquals("boom", model.getAttribute("message"));
  }
}
