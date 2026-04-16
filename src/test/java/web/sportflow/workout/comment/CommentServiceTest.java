package web.sportflow.workout.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.notification.NotificationService;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock private CommentRepository commentRepo;
  @Mock private WorkoutRepository workoutRepo;
  @Mock private UserRepository userRepo;
  @Mock private NotificationService notificationService;

  @InjectMocks private CommentService commentService;

  @Test
  void addComment_savesCommentAndTriggersNotification() {
    Workout workout = new Workout();
    workout.setId(10L);
    User author = user(5L, Role.USER);

    when(workoutRepo.findById(10L)).thenReturn(Optional.of(workout));
    when(userRepo.findByEmail("alice@demo.local")).thenReturn(Optional.of(author));

    commentService.addComment(10L, "alice@demo.local", "Bravo");

    verify(commentRepo).save(any(Comment.class));
    verify(notificationService).notifyCommentOnWorkout(workout, author);
  }

  @Test
  void addComment_throwsWhenWorkoutOrUserNotFound() {
    when(workoutRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> commentService.addComment(1L, "u@d.local", "x"));

    Workout workout = new Workout();
    when(workoutRepo.findById(2L)).thenReturn(Optional.of(workout));
    when(userRepo.findByEmail("u@d.local")).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> commentService.addComment(2L, "u@d.local", "x"));
  }

  @Test
  void getCommentsForWorkout_delegatesToRepository() {
    List<Comment> comments = List.of(new Comment());
    when(commentRepo.findByWorkoutIdOrderByCreatedAtAsc(3L)).thenReturn(comments);

    assertEquals(comments, commentService.getCommentsForWorkout(3L));
  }

  @Test
  void deleteComment_handlesPermissionChecksAndDeletion() {
    Workout workout = new Workout();
    workout.setId(10L);

    User author = user(5L, Role.USER);
    Comment comment = new Comment("Hello", workout, author);
    when(commentRepo.findById(7L)).thenReturn(Optional.of(comment));

    assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(10L, 7L, null));

    User other = user(8L, Role.USER);
    assertThrows(
        IllegalArgumentException.class, () -> commentService.deleteComment(10L, 7L, other));

    User admin = user(9L, Role.ADMIN);
    commentService.deleteComment(10L, 7L, admin);
    verify(commentRepo).delete(comment);
  }

  @Test
  void deleteComment_rejectsInvalidWorkoutBinding() {
    Workout workout = new Workout();
    workout.setId(99L);
    Comment comment = new Comment("Hi", workout, user(5L, Role.USER));
    when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));

    assertThrows(
        IllegalArgumentException.class,
        () -> commentService.deleteComment(10L, 1L, user(5L, Role.USER)));
  }

  private User user(Long id, Role role) {
    User user = new User("First", "Last", "mail@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(role);
    return user;
  }
}
