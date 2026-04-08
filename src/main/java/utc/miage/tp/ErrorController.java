package utc.miage.tp;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {

  private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

  @ExceptionHandler(Throwable.class)
  public String handleException(
      final Throwable throwable, final Model model, HttpServletResponse response) {
    logger.error("Unexpected error", throwable);

    // Default to 500, but you could check for specific types here
    int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    String reason = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

    model.addAttribute("httpStatus", status);
    model.addAttribute("reason", reason);
    model.addAttribute("message", throwable != null ? throwable.getMessage() : "Unknown Error");

    return "error";
  }
}
