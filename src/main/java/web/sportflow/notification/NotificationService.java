package web.sportflow.notification;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.friendship.Friendship;
import web.sportflow.user.User;
import web.sportflow.workout.Workout;

@Service
public class NotificationService {

  private static final String DEFAULT_TARGET_URL = "/users/workout";

  private final NotificationRepository notificationRepository;

  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional(readOnly = true)
  public List<Notification> getRecentForUser(User user, int limit) {
    if (user == null || user.getId() == null || limit <= 0) {
      return List.of();
    }
    return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(
        user.getId(), PageRequest.of(0, limit));
  }

  @Transactional(readOnly = true)
  public long countUnread(User user) {
    if (user == null || user.getId() == null) {
      return 0L;
    }
    return notificationRepository.countByRecipientIdAndReadFalse(user.getId());
  }

  @Transactional
  public void markAsRead(Long notificationId, User currentUser) {
    if (notificationId == null || currentUser == null || currentUser.getId() == null) {
      return;
    }

    notificationRepository
        .findByIdAndRecipientId(notificationId, currentUser.getId())
        .ifPresent(
            notification -> {
              if (!notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
              }
            });
  }

  @Transactional
  public void markAllAsRead(User currentUser) {
    if (currentUser == null || currentUser.getId() == null) {
      return;
    }
    notificationRepository.markAllAsRead(currentUser.getId());
  }

  @Transactional
  public void notifyKudoOnWorkout(Workout workout, User actor) {
    if (workout == null || workout.getUser() == null || workout.getUser().getId() == null) {
      return;
    }

    String actorName = resolveActorName(actor);
    String workoutName = resolveWorkoutName(workout);
    String message = actorName + " a réagi à ta séance " + workoutName + ".";
    createNotification(
        workout.getUser(), actor, NotificationType.KUDO, message, DEFAULT_TARGET_URL);
  }

  @Transactional
  public void notifyCommentOnWorkout(Workout workout, User actor) {
    if (workout == null || workout.getUser() == null || workout.getUser().getId() == null) {
      return;
    }

    String actorName = resolveActorName(actor);
    String workoutName = resolveWorkoutName(workout);
    String message = actorName + " a commenté ta séance " + workoutName + ".";
    createNotification(
        workout.getUser(), actor, NotificationType.COMMENT, message, DEFAULT_TARGET_URL);
  }

  @Transactional
  public void notifyFriendRequestReceived(Friendship friendship) {
    if (friendship == null || friendship.getAddressee() == null) {
      return;
    }
    User recipient = friendship.getAddressee();
    User actor = friendship.getRequester();
    String actorName = resolveActorName(actor);
    String message = actorName + " t'a envoyé une demande d'ami.";
    createNotification(
        recipient, actor, NotificationType.FRIEND_REQUEST_RECEIVED, message, "/users/friends");
  }

  @Transactional
  public void notifyFriendRequestAccepted(Friendship friendship, User acceptedByUser) {
    if (friendship == null || friendship.getRequester() == null) {
      return;
    }
    User recipient = friendship.getRequester();
    String actorName = resolveActorName(acceptedByUser);
    String message = actorName + " a accepté ta demande d'ami.";
    createNotification(
        recipient,
        acceptedByUser,
        NotificationType.FRIEND_REQUEST_ACCEPTED,
        message,
        "/users/friends");
  }

  @Transactional
  public void notifyFriendRequestRefused(Friendship friendship, User refusedByUser) {
    if (friendship == null || friendship.getRequester() == null) {
      return;
    }
    User recipient = friendship.getRequester();
    String actorName = resolveActorName(refusedByUser);
    String message = actorName + " a refusé ta demande d'ami.";
    createNotification(
        recipient,
        refusedByUser,
        NotificationType.FRIEND_REQUEST_REFUSED,
        message,
        "/users/friends");
  }

  private void createNotification(
      User recipient, User actor, NotificationType type, String message, String targetUrl) {
    if (recipient == null || recipient.getId() == null || message == null || message.isBlank()) {
      return;
    }
    if (actor != null && actor.getId() != null && actor.getId().equals(recipient.getId())) {
      return;
    }

    Notification notification = new Notification(recipient, actor, type, message, targetUrl);
    notificationRepository.save(notification);
  }

  private String resolveActorName(User actor) {
    if (actor == null) {
      return "Quelqu'un";
    }
    String firstname = actor.getFirstname() == null ? "" : actor.getFirstname().trim();
    String lastname = actor.getLastname() == null ? "" : actor.getLastname().trim();
    String displayName = (firstname + " " + lastname).trim();
    return displayName.isBlank() ? "Quelqu'un" : displayName;
  }

  private String resolveWorkoutName(Workout workout) {
    if (workout == null) {
      return "sportive";
    }
    if (workout.getName() != null && !workout.getName().isBlank()) {
      return "\"" + workout.getName().trim() + "\"";
    }
    if (workout.getSport() != null
        && workout.getSport().getDisplayName() != null
        && !workout.getSport().getDisplayName().isBlank()) {
      return "(" + workout.getSport().getDisplayName().trim() + ")";
    }
    return "sportive";
  }
}
