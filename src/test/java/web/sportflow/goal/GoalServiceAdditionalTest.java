package web.sportflow.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class GoalServiceAdditionalTest {

  @Mock private GoalRepository goalRepository;
  @Mock private FriendshipService friendshipService;
  @Mock private UserRepository userRepository;

  @InjectMocks private GoalService goalService;

  @Test
  void getFriendsAndUserGoal_mergesAndDeduplicatesGoals() {
    User current = new User("A", "B", "a@b.c", 70.0, 180.0, Sex.MALE);
    current.setId(1L);
    current.setRole(Role.USER);

    User friend = new User("F", "R", "f@r.c", 70.0, 180.0, Sex.MALE);
    friend.setId(2L);

    Goal shared = new Goal("Shared", GoalType.DISTANCE, 10.0, 2.0, "km", friend);
    shared.setId(10L);
    friend.getGoals().add(shared);

    when(friendshipService.getCurrentUserAndFriend(current)).thenReturn(List.of(current, friend));
    when(userRepository.findByIdIn(List.of(1L, 2L))).thenReturn(List.of(current, friend));
    when(goalRepository.findByUserIn(List.of(current, friend))).thenReturn(List.of(shared));

    List<Goal> result = goalService.getFriendsAndUserGoal(current);

    assertEquals(1, result.size());
    assertEquals(shared, result.getFirst());
  }
}
