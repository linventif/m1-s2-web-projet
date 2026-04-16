package web.sportflow.notification;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import web.sportflow.user.User;

@Controller
@RequestMapping({"/users/notifications", "/user/notifications"})
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping("/{notificationId}/read")
  public String markNotificationAsRead(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Long notificationId,
      @RequestParam(defaultValue = "/users/dashboard") String returnTo) {
    notificationService.markAsRead(notificationId, currentUser);
    return "redirect:" + resolveReturnTo(returnTo);
  }

  @PostMapping("/read-all")
  public String markAllNotificationsAsRead(
      @AuthenticationPrincipal User currentUser,
      @RequestParam(defaultValue = "/users/dashboard") String returnTo) {
    notificationService.markAllAsRead(currentUser);
    return "redirect:" + resolveReturnTo(returnTo);
  }

  private String resolveReturnTo(String returnTo) {
    if (returnTo != null
        && (returnTo.startsWith("/users/")
            || returnTo.startsWith("/user/")
            || returnTo.startsWith("/admin"))) {
      return returnTo;
    }
    return "/users/dashboard";
  }
}
