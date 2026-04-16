package web.sportflow.notification;

import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import web.sportflow.user.User;

@ControllerAdvice
public class NotificationModelAttributes {

  private static final int HEADER_NOTIFICATIONS_LIMIT = 8;
  private static final String DEFAULT_RETURN_TO = "/users/dashboard";

  private final NotificationService notificationService;

  public NotificationModelAttributes(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @ModelAttribute
  public void addNotificationAttributes(Model model) {
    model.addAttribute("notificationReturnTo", resolveNotificationReturnTo());

    User currentUser = resolveCurrentUser();
    if (currentUser == null || currentUser.getId() == null) {
      model.addAttribute("headerNotifications", List.of());
      model.addAttribute("headerUnreadNotificationsCount", 0L);
      return;
    }

    model.addAttribute(
        "headerNotifications",
        notificationService.getRecentForUser(currentUser, HEADER_NOTIFICATIONS_LIMIT));
    model.addAttribute(
        "headerUnreadNotificationsCount", notificationService.countUnread(currentUser));
  }

  private User resolveCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof User user) {
      return user;
    }
    return null;
  }

  private String resolveNotificationReturnTo() {
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
      return DEFAULT_RETURN_TO;
    }

    String requestUri = servletAttributes.getRequest().getRequestURI();
    if (requestUri == null || requestUri.isBlank()) {
      return DEFAULT_RETURN_TO;
    }
    return requestUri;
  }
}
