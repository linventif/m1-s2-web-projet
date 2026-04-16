package web.sportflow.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class NotificationControllerTest {

  private final NotificationService notificationService = Mockito.mock(NotificationService.class);
  private final NotificationController controller = new NotificationController(notificationService);

  @Test
  void markNotificationAsRead_redirectsToSafeUserPath() {
    User currentUser = user(1L);

    String view = controller.markNotificationAsRead(currentUser, 10L, "/users/challenges");

    assertEquals("redirect:/users/challenges", view);
    verify(notificationService).markAsRead(10L, currentUser);
  }

  @Test
  void markNotificationAsRead_rejectsUnsafeReturnPath() {
    User currentUser = user(1L);

    String view = controller.markNotificationAsRead(currentUser, 10L, "https://evil.example");

    assertEquals("redirect:/users/dashboard", view);
  }

  @Test
  void markAllNotificationsAsRead_supportsAdminAndUserPrefixes() {
    User currentUser = user(2L);

    String adminView = controller.markAllNotificationsAsRead(currentUser, "/admin/panel");
    String singularView = controller.markAllNotificationsAsRead(currentUser, "/user/notifications");

    assertEquals("redirect:/admin/panel", adminView);
    assertEquals("redirect:/user/notifications", singularView);
    verify(notificationService, times(2)).markAllAsRead(currentUser);
  }

  private User user(Long id) {
    User user = new User("John", "Doe", "john@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
