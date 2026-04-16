package web.sportflow.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class NotificationModelAttributesTest {

  private final NotificationService notificationService =
      org.mockito.Mockito.mock(NotificationService.class);
  private final NotificationModelAttributes modelAttributes =
      new NotificationModelAttributes(notificationService);

  @AfterEach
  void cleanContexts() {
    SecurityContextHolder.clearContext();
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void addNotificationAttributes_setsDefaultsForAnonymousUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(
            new AnonymousAuthenticationToken(
                "key", "anonymous", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));

    Model model = new ExtendedModelMap();
    modelAttributes.addNotificationAttributes(model);

    assertEquals("/users/dashboard", model.getAttribute("notificationReturnTo"));
    assertEquals(List.of(), model.getAttribute("headerNotifications"));
    assertEquals(0L, model.getAttribute("headerUnreadNotificationsCount"));
  }

  @Test
  void addNotificationAttributes_usesCurrentUriAndLoadsNotificationsForAuthenticatedUser() {
    User currentUser = user(12L);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                currentUser, "secret", currentUser.getAuthorities()));

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/users/dashboard");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    List<Notification> notifications =
        List.of(new Notification(currentUser, null, NotificationType.KUDO, "x", "/users/workout"));
    when(notificationService.getRecentForUser(currentUser, 8)).thenReturn(notifications);
    when(notificationService.countUnread(currentUser)).thenReturn(5L);

    Model model = new ExtendedModelMap();
    modelAttributes.addNotificationAttributes(model);

    assertEquals("/users/dashboard", model.getAttribute("notificationReturnTo"));
    assertEquals(notifications, model.getAttribute("headerNotifications"));
    assertEquals(5L, model.getAttribute("headerUnreadNotificationsCount"));
    verify(notificationService).getRecentForUser(currentUser, 8);
    verify(notificationService).countUnread(currentUser);
  }

  @Test
  void addNotificationAttributes_fallsBackToDefaultWhenAuthenticationPrincipalIsNotUser() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("plain-principal", "pwd"));

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    Model model = new ExtendedModelMap();
    modelAttributes.addNotificationAttributes(model);

    assertEquals("/users/dashboard", model.getAttribute("notificationReturnTo"));
    assertEquals(List.of(), model.getAttribute("headerNotifications"));
    assertEquals(0L, model.getAttribute("headerUnreadNotificationsCount"));
  }

  private User user(Long id) {
    User user = new User("Alice", "Martin", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
