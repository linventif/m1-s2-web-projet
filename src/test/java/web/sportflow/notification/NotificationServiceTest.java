package web.sportflow.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.friendship.Friendship;
import web.sportflow.friendship.FriendshipStatus;
import web.sportflow.sport.Sport;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.workout.Workout;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationService notificationService;

  @Test
  void getRecentForUser_returnsEmptyForInvalidArguments() {
    assertTrue(notificationService.getRecentForUser(null, 8).isEmpty());
    assertTrue(notificationService.getRecentForUser(user(null, "A", "B"), 8).isEmpty());
    assertTrue(notificationService.getRecentForUser(user(1L, "A", "B"), 0).isEmpty());
    verify(notificationRepository, never()).findByRecipientIdOrderByCreatedAtDesc(any(), any());
  }

  @Test
  void getRecentForUser_usesRepositoryForValidInput() {
    User recipient = user(5L, "Alice", "Martin");
    Notification first =
        new Notification(recipient, null, NotificationType.KUDO, "x", "/users/workout");
    when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc(any(), any()))
        .thenReturn(List.of(first));

    List<Notification> result = notificationService.getRecentForUser(recipient, 3);

    assertEquals(1, result.size());
    assertEquals(first, result.getFirst());
  }

  @Test
  void countUnread_returnsZeroForInvalidUserOtherwiseRepositoryValue() {
    assertEquals(0L, notificationService.countUnread(null));
    assertEquals(0L, notificationService.countUnread(user(null, "A", "B")));

    User recipient = user(8L, "Bob", "Durand");
    when(notificationRepository.countByRecipientIdAndReadFalse(8L)).thenReturn(4L);

    assertEquals(4L, notificationService.countUnread(recipient));
  }

  @Test
  void markAsRead_updatesOnlyUnreadNotificationsOwnedByCurrentUser() {
    User currentUser = user(7L, "Alice", "Martin");
    Notification unread =
        new Notification(currentUser, null, NotificationType.COMMENT, "message", "/users/workout");
    unread.setRead(false);
    when(notificationRepository.findByIdAndRecipientId(10L, 7L)).thenReturn(Optional.of(unread));

    notificationService.markAsRead(10L, currentUser);

    assertTrue(unread.isRead());
    verify(notificationRepository).save(unread);
  }

  @Test
  void markAsRead_skipsWhenAlreadyReadOrInvalidInput() {
    User currentUser = user(7L, "Alice", "Martin");
    Notification read =
        new Notification(currentUser, null, NotificationType.COMMENT, "message", "/users/workout");
    read.setRead(true);
    when(notificationRepository.findByIdAndRecipientId(11L, 7L)).thenReturn(Optional.of(read));

    notificationService.markAsRead(11L, currentUser);
    notificationService.markAsRead(null, currentUser);
    notificationService.markAsRead(11L, null);

    verify(notificationRepository, never()).save(read);
  }

  @Test
  void markAllAsRead_callsRepositoryOnlyForValidUser() {
    notificationService.markAllAsRead(null);
    notificationService.markAllAsRead(user(null, "A", "B"));

    User currentUser = user(42L, "John", "Doe");
    notificationService.markAllAsRead(currentUser);

    verify(notificationRepository).markAllAsRead(42L);
  }

  @Test
  void notifyKudoOnWorkout_createsNotificationWithWorkoutName() {
    User recipient = user(1L, "Judy", "Hopps");
    User actor = user(2L, "Nick", "Wilde");
    Workout workout = new Workout();
    workout.setName("Session tempo");
    workout.setUser(recipient);

    notificationService.notifyKudoOnWorkout(workout, actor);

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification saved = captor.getValue();
    assertEquals(NotificationType.KUDO, saved.getType());
    assertTrue(saved.getMessage().contains("Nick Wilde"));
    assertTrue(saved.getMessage().contains("\"Session tempo\""));
  }

  @Test
  void notifyCommentOnWorkout_usesSportDisplayNameFallback() {
    User recipient = user(1L, "Judy", "Hopps");
    User actor = user(3L, "", "");
    Sport sport = new Sport("Course", 9.0);

    Workout workout = new Workout();
    workout.setUser(recipient);
    workout.setSport(sport);

    notificationService.notifyCommentOnWorkout(workout, actor);

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification saved = captor.getValue();
    assertEquals(NotificationType.COMMENT, saved.getType());
    assertTrue(saved.getMessage().contains("Quelqu'un"));
    assertTrue(saved.getMessage().contains("(Course)"));
  }

  @Test
  void notifyWorkoutActions_skipsInvalidOrSelfRecipient() {
    User sameUser = user(9L, "Alice", "Same");
    Workout workout = new Workout();
    workout.setUser(sameUser);

    notificationService.notifyKudoOnWorkout(null, sameUser);
    notificationService.notifyCommentOnWorkout(new Workout(), sameUser);
    notificationService.notifyKudoOnWorkout(workout, sameUser);

    verify(notificationRepository, never()).save(any(Notification.class));
  }

  @Test
  void notifyFriendshipEvents_createExpectedNotifications() {
    User requester = user(4L, "A", "Requester");
    User addressee = user(5L, "B", "Addressee");
    User actor = user(6L, "C", "Actor");

    Friendship friendship = new Friendship(requester, addressee, FriendshipStatus.PENDING);

    notificationService.notifyFriendRequestReceived(friendship);
    notificationService.notifyFriendRequestAccepted(friendship, actor);
    notificationService.notifyFriendRequestRefused(friendship, actor);

    verify(notificationRepository, times(3)).save(any(Notification.class));
  }

  @Test
  void notifyFriendshipEvents_skipInvalidFriendships() {
    notificationService.notifyFriendRequestReceived(null);
    notificationService.notifyFriendRequestAccepted(null, null);
    notificationService.notifyFriendRequestRefused(null, null);

    Friendship missingRequester = new Friendship();
    Friendship missingAddressee = new Friendship();
    missingAddressee.setRequester(user(1L, "x", "y"));

    notificationService.notifyFriendRequestAccepted(missingRequester, null);
    notificationService.notifyFriendRequestRefused(missingRequester, null);
    notificationService.notifyFriendRequestReceived(missingAddressee);

    verify(notificationRepository, never()).save(any(Notification.class));
  }

  private User user(Long id, String firstname, String lastname) {
    User user = new User(firstname, lastname, firstname + "@demo.local", 70.0, 175.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
