package web.sportflow.friendship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

  @Mock private FriendshipRepository friendshipRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private FriendshipService friendshipService;

  @Test
  void sendRequest_createsPendingRequest_whenNoRelationshipExists() {
    User requester = user(1L, "requester@demo.local");
    User addressee = user(2L, "addressee@demo.local");
    when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.empty());
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Friendship result = friendshipService.sendRequest(1L, 2L);

    assertEquals(FriendshipStatus.PENDING, result.getStatus());
    assertEquals(requester, result.getRequester());
    assertEquals(addressee, result.getAddressee());
  }

  @Test
  void sendRequest_autoAccepts_whenOppositePendingRequestExists() {
    User requester = user(1L, "requester@demo.local");
    User addressee = user(2L, "addressee@demo.local");
    Friendship existing = new Friendship(addressee, requester, FriendshipStatus.PENDING);

    when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(existing));
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Friendship result = friendshipService.sendRequest(1L, 2L);

    assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
    assertEquals(addressee, result.getRequester());
    assertEquals(requester, result.getAddressee());
  }

  @Test
  void sendRequest_throws_whenSameDirectionPendingRequestAlreadyExists() {
    User requester = user(1L, "requester@demo.local");
    User addressee = user(2L, "addressee@demo.local");
    Friendship existing = new Friendship(requester, addressee, FriendshipStatus.PENDING);

    when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(existing));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> friendshipService.sendRequest(1L, 2L));

    assertEquals("A request is already pending.", exception.getMessage());
  }

  @Test
  void sendRequest_throws_whenUsersAreAlreadyFriends() {
    User requester = user(1L, "requester@demo.local");
    User addressee = user(2L, "addressee@demo.local");
    Friendship existing = new Friendship(requester, addressee, FriendshipStatus.ACCEPTED);

    when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(existing));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> friendshipService.sendRequest(1L, 2L));

    assertEquals("You are already friends with this user.", exception.getMessage());
  }

  @Test
  void unfriend_deletesFriendship_whenRelationshipIsAccepted() {
    Friendship existing =
        new Friendship(
            user(1L, "requester@demo.local"),
            user(2L, "addressee@demo.local"),
            FriendshipStatus.ACCEPTED);
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(existing));

    friendshipService.unfriend(1L, 2L);

    verify(friendshipRepository).delete(existing);
  }

  @Test
  void unfriend_throws_whenRelationshipIsNotAccepted() {
    Friendship existing =
        new Friendship(
            user(1L, "requester@demo.local"),
            user(2L, "addressee@demo.local"),
            FriendshipStatus.PENDING);
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(existing));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> friendshipService.unfriend(1L, 2L));

    assertEquals("Only accepted friendships can be removed.", exception.getMessage());
  }

  private User user(Long id, String email) {
    User user = new User("User " + id, "Test" + id, email, 70.0, 175.0, Sex.MALE);
    user.setId(id);
    user.setPassword("secret");
    user.setRole(Role.USER);
    return user;
  }
}
