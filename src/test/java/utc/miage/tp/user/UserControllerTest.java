package utc.miage.tp.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import utc.miage.tp.friendship.Friendship;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.friendship.FriendshipStatus;
import utc.miage.tp.workout.WorkoutService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @Mock private WorkoutService workoutService;

  @Mock private FriendshipService friendshipService;

  @InjectMocks private UserController userController;

  @Test
  void sendFriendRequest_setsAutoAcceptedMessage_whenCrossPendingRequestExists() {
    User currentUser = new User("Alice", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    Friendship accepted = new Friendship();
    accepted.setStatus(FriendshipStatus.ACCEPTED);
    when(friendshipService.sendRequest(1L, 2L)).thenReturn(accepted);

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.sendFriendRequest(currentUser, 2L, "/users/users", redirect);

    assertEquals("redirect:/users/users", view);
    assertEquals("Friend request auto-accepted.", redirect.getFlashAttributes().get("message"));
  }

  @Test
  void sendFriendRequest_setsSentMessage_whenRequestIsPending() {
    User currentUser = new User("Alice", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    Friendship pending = new Friendship();
    pending.setStatus(FriendshipStatus.PENDING);
    when(friendshipService.sendRequest(1L, 2L)).thenReturn(pending);

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.sendFriendRequest(currentUser, 2L, "/users/friends", redirect);

    assertEquals("redirect:/users/friends", view);
    assertEquals("Friend request sent.", redirect.getFlashAttributes().get("message"));
    assertNull(redirect.getFlashAttributes().get("errorMessage"));
  }

  @Test
  void sendFriendRequest_rejectsUnsafeReturnPath() {
    User currentUser = new User("Alice", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    Friendship pending = new Friendship();
    pending.setStatus(FriendshipStatus.PENDING);
    when(friendshipService.sendRequest(1L, 2L)).thenReturn(pending);

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.sendFriendRequest(currentUser, 2L, "https://evil.test", redirect);

    assertEquals("redirect:/users/friends", view);
  }
}
