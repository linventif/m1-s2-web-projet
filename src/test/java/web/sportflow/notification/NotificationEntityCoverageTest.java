package web.sportflow.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class NotificationEntityCoverageTest {

  @Test
  void getters_setters_and_prepersist_coverEntityBranches() {
    User recipient = user("recipient@mail.local");
    User actor = user("actor@mail.local");
    Notification notification =
        new Notification(
            recipient, actor, NotificationType.COMMENT, "Nouveau commentaire", "/post/1");

    notification.setId(42L);
    notification.setRead(true);
    notification.setTargetUrl("/post/2");
    notification.setType(NotificationType.KUDO);
    notification.setMessage("Bravo");
    notification.setActor(null);
    notification.setCreatedAt(null);
    notification.prePersist();

    assertEquals(42L, notification.getId());
    assertEquals(recipient, notification.getRecipient());
    assertNull(notification.getActor());
    assertEquals(NotificationType.KUDO, notification.getType());
    assertEquals("Bravo", notification.getMessage());
    assertEquals("/post/2", notification.getTargetUrl());
    assertTrue(notification.isRead());
    assertNotNull(notification.getCreatedAt());

    LocalDateTime fixed = LocalDateTime.of(2026, 4, 16, 10, 30);
    notification.setCreatedAt(fixed);
    notification.prePersist();
    assertEquals(fixed, notification.getCreatedAt());
  }

  private static User user(String email) {
    return new User(
        "First",
        "Last",
        email,
        "password",
        70.0,
        178.0,
        Sex.MALE,
        LocalDate.of(1992, 6, 15),
        PracticeLevel.INTERMEDIATE);
  }
}
