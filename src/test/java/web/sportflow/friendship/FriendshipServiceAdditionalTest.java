package web.sportflow.friendship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import web.sportflow.notification.NotificationService;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FriendshipServiceAdditionalTest {

  @Mock private FriendshipRepository friendshipRepository;
  @Mock private UserRepository userRepository;
  @Mock private NotificationService notificationService;

  @InjectMocks private FriendshipService friendshipService;

  @Test
  void sendRequest_validatesSelfRequestAndRefusedReuseFlow() {
    assertThrows(IllegalArgumentException.class, () -> friendshipService.sendRequest(1L, 1L));

    User requester = user(1L);
    User addressee = user(2L);
    Friendship refused = new Friendship(addressee, requester, FriendshipStatus.REFUSED);

    when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(refused));
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Friendship result = friendshipService.sendRequest(1L, 2L);

    assertEquals(FriendshipStatus.PENDING, result.getStatus());
    verify(notificationService).notifyFriendRequestReceived(result);
  }

  @Test
  void acceptAndRefuseRequest_coverAuthorizationAndStatusChecks() {
    User requester = user(1L);
    User addressee = user(2L);
    Friendship friendship = new Friendship(requester, addressee, FriendshipStatus.PENDING);
    friendship.setId(10L);

    when(friendshipRepository.findById(10L)).thenReturn(Optional.of(friendship));
    when(userRepository.findById(2L)).thenReturn(Optional.of(addressee));
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Friendship accepted = friendshipService.acceptRequest(2L, 10L);
    assertEquals(FriendshipStatus.ACCEPTED, accepted.getStatus());

    friendship.setStatus(FriendshipStatus.PENDING);
    Friendship refused = friendshipService.refuseRequest(2L, 10L);
    assertEquals(FriendshipStatus.REFUSED, refused.getStatus());

    friendship.setStatus(FriendshipStatus.ACCEPTED);
    assertThrows(IllegalArgumentException.class, () -> friendshipService.acceptRequest(2L, 10L));
    assertThrows(IllegalArgumentException.class, () -> friendshipService.refuseRequest(2L, 10L));
    assertThrows(IllegalArgumentException.class, () -> friendshipService.acceptRequest(3L, 10L));
    assertThrows(IllegalArgumentException.class, () -> friendshipService.refuseRequest(3L, 10L));
  }

  @Test
  void readMethodsAndCreateAcceptedFriendship_coverRemainingBranches() {
    User current = user(8L);
    User friend = user(9L);
    Friendship accepted = new Friendship(current, friend, FriendshipStatus.ACCEPTED);
    accepted.setId(77L);

    when(friendshipRepository.findByAddresseeIdAndStatus(8L, FriendshipStatus.PENDING))
        .thenReturn(List.of());
    when(friendshipRepository.findByRequesterIdAndStatus(8L, FriendshipStatus.PENDING))
        .thenReturn(List.of());
    when(friendshipRepository.findAcceptedForUser(8L)).thenReturn(List.of(accepted));

    assertTrue(friendshipService.getIncomingPendingRequests(8L).isEmpty());
    assertTrue(friendshipService.getOutgoingPendingRequests(8L).isEmpty());
    assertEquals(1, friendshipService.getAcceptedFriendships(8L).size());

    List<Long> visibleIds = friendshipService.getCurrentUserAndFriendIds(8L);
    assertEquals(List.of(9L, 8L), visibleIds);
    assertTrue(friendshipService.getCurrentUserAndFriendIds(null).isEmpty());

    assertEquals(2, friendshipService.getCurrentUserAndFriend(current).size());
    assertTrue(friendshipService.getCurrentUserAndFriend(null).isEmpty());

    assertTrue(friendshipService.findRelationshipBetween(null, 1L).isEmpty());
    assertTrue(friendshipService.findRelationshipBetween(1L, null).isEmpty());

    when(friendshipRepository.findRelationshipBetween(8L, 9L)).thenReturn(Optional.of(accepted));
    when(userRepository.findById(8L)).thenReturn(Optional.of(current));
    when(userRepository.findById(9L)).thenReturn(Optional.of(friend));
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Friendship result = friendshipService.createAcceptedFriendship(8L, 9L);
    assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());

    when(friendshipRepository.findRelationshipBetween(8L, 10L)).thenReturn(Optional.empty());
    when(userRepository.findById(10L)).thenReturn(Optional.of(user(10L)));
    assertEquals(
        FriendshipStatus.ACCEPTED, friendshipService.createAcceptedFriendship(8L, 10L).getStatus());

    assertThrows(
        IllegalArgumentException.class, () -> friendshipService.createAcceptedFriendship(8L, 8L));
    when(friendshipRepository.findRelationshipBetween(11L, 12L)).thenReturn(Optional.empty());
    when(userRepository.findById(11L)).thenReturn(Optional.empty());
    assertThrows(
        IllegalArgumentException.class, () -> friendshipService.createAcceptedFriendship(11L, 12L));
  }

  @Test
  void unfriend_throwsWhenNoRelationship() {
    when(friendshipRepository.findRelationshipBetween(1L, 2L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> friendshipService.unfriend(1L, 2L));
    verify(friendshipRepository, never()).delete(any());
  }

  private User user(Long id) {
    User user = new User("User", "Test", "user" + id + "@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
