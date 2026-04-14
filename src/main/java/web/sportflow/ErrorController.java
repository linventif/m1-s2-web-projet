package web.sportflow;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ErrorController {

  private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

  @ExceptionHandler(NoResourceFoundException.class)
  public String handleNoResource(
      final NoResourceFoundException exception,
      final Model model,
      final HttpServletResponse response) {
    logger.warn("Resource not found: {}", exception.getResourcePath());
    response.setStatus(HttpStatus.NOT_FOUND.value());
    model.addAttribute("httpStatus", HttpStatus.NOT_FOUND.value());
    model.addAttribute("reason", HttpStatus.NOT_FOUND.getReasonPhrase());
    model.addAttribute("message", exception.getMessage());
    return "error";
  }

  @ExceptionHandler(Throwable.class)
  public String handleException(
      final Throwable throwable, final Model model, HttpServletResponse response) {
    logger.error("Unexpected error", throwable);

    int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    String reason = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

    response.setStatus(status);
    model.addAttribute("httpStatus", status);
    model.addAttribute("reason", reason);
    model.addAttribute("message", throwable != null ? throwable.getMessage() : "Unknown Error");

    return "error";
  }
}
