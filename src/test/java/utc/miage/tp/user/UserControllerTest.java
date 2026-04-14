package utc.miage.tp.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import utc.miage.tp.badge.BadgeService;
import utc.miage.tp.challenge.ChallengeService;
import utc.miage.tp.friendship.Friendship;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.friendship.FriendshipStatus;
import utc.miage.tp.goal.GoalService;
import utc.miage.tp.workout.WorkoutService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock private UserService userService;

  @Mock private WorkoutService workoutService;

  @Mock private GoalService goalService;

  @Mock private ChallengeService challengeService;

  @Mock private BadgeService badgeService;

  @Mock private FriendshipService friendshipService;

  @InjectMocks private UserController userController;

  @Test
  void sendFriendRequest_setsAutoAcceptedMessage_whenCrossPendingRequestExists() {
    User currentUser = new User("Alice", "Rondo", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
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
    User currentUser = new User("Alice", "Rondo", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
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
    User currentUser = new User("Alice", "Ronde", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    Friendship pending = new Friendship();
    pending.setStatus(FriendshipStatus.PENDING);
    when(friendshipService.sendRequest(1L, 2L)).thenReturn(pending);

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.sendFriendRequest(currentUser, 2L, "https://evil.test", redirect);

    assertEquals("redirect:/users/friends", view);
  }

  @Test
  void showUserProfile_marksFriendRequestSent_whenPendingOutgoingRelationshipExists() {
    User currentUser = new User("Alice", "Rondo", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    User profileUser = new User("Bob", "Marin", "bob@demo.local", 72.0, 180.0, Sex.MALE);
    profileUser.setId(2L);

    Friendship relationship = new Friendship();
    relationship.setId(10L);
    relationship.setStatus(FriendshipStatus.PENDING);
    relationship.setRequester(currentUser);
    relationship.setAddressee(profileUser);

    when(userService.getUserById(2L)).thenReturn(Optional.of(profileUser));
    when(friendshipService.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(relationship));
    when(workoutService.getAll()).thenReturn(List.of());
    when(badgeService.getAll()).thenReturn(List.of());

    Model model = new ExtendedModelMap();
    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.showUserProfile(currentUser, 2L, model, redirect);

    assertEquals("user-profile", view);
    assertEquals("Demande envoyee", model.getAttribute("friendshipStatusLabel"));
    assertEquals(Boolean.FALSE, model.getAttribute("canSendFriendRequest"));
  }

  @Test
  void showUserProfile_exposesAcceptActions_whenPendingIncomingRelationshipExists() {
    User currentUser = new User("Alice", "Rondo", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    currentUser.setId(1L);
    User profileUser = new User("Bob", "Marin", "bob@demo.local", 72.0, 180.0, Sex.MALE);
    profileUser.setId(2L);

    Friendship relationship = new Friendship();
    relationship.setId(42L);
    relationship.setStatus(FriendshipStatus.PENDING);
    relationship.setRequester(profileUser);
    relationship.setAddressee(currentUser);

    when(userService.getUserById(2L)).thenReturn(Optional.of(profileUser));
    when(friendshipService.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(relationship));
    when(workoutService.getAll()).thenReturn(List.of());
    when(badgeService.getAll()).thenReturn(List.of());

    Model model = new ExtendedModelMap();
    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    String view = userController.showUserProfile(currentUser, 2L, model, redirect);

    assertEquals("user-profile", view);
    assertEquals("Demande recue", model.getAttribute("friendshipStatusLabel"));
    assertEquals(Boolean.TRUE, model.getAttribute("canAcceptFriendRequest"));
    assertEquals(42L, model.getAttribute("incomingFriendshipId"));
    assertEquals(Boolean.FALSE, model.getAttribute("canSendFriendRequest"));
  }
}
