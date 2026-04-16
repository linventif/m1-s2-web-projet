package web.sportflow.friendship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class FriendshipEntityCoverageTest {

  @Test
  void lifecycle_and_accessors_coverEntityBranches() {
    User requester = user("requester@mail.local");
    User addressee = user("addressee@mail.local");
    Friendship friendship = new Friendship(requester, addressee, FriendshipStatus.PENDING);

    friendship.setId(99L);
    friendship.onCreate();
    assertNotNull(friendship.getCreatedAt());
    assertNotNull(friendship.getUpdatedAt());
    assertTrue(!friendship.getUpdatedAt().isBefore(friendship.getCreatedAt()));

    LocalDateTime oldUpdatedAt = friendship.getUpdatedAt();
    friendship.setStatus(FriendshipStatus.ACCEPTED);
    friendship.onUpdate();

    assertEquals(99L, friendship.getId());
    assertEquals(requester, friendship.getRequester());
    assertEquals(addressee, friendship.getAddressee());
    assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
    assertTrue(!friendship.getUpdatedAt().isBefore(oldUpdatedAt));
  }

  private static User user(String email) {
    return new User(
        "First",
        "Last",
        email,
        "password",
        68.0,
        170.0,
        Sex.FEMALE,
        LocalDate.of(1994, 4, 10),
        PracticeLevel.BEGINNER);
  }
}
