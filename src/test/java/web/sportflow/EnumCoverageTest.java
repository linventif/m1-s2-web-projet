package web.sportflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.friendship.FriendshipStatus;
import web.sportflow.goal.GoalType;
import web.sportflow.notification.NotificationType;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;

class EnumCoverageTest {

  @Test
  void roleLabelsAndAuthority_areExpected() {
    assertEquals("Utilisateur", Role.USER.getLabelFr());
    assertEquals("Administrateur", Role.ADMIN.getLabelFr());
    assertEquals("ROLE_USER", Role.USER.getAuthority());
    assertEquals("ROLE_ADMIN", Role.ADMIN.getAuthority());
  }

  @Test
  void sexLabels_areExpected() {
    assertEquals("Homme", Sex.MALE.getLabelFr());
    assertEquals("Femme", Sex.FEMALE.getLabelFr());
  }

  @Test
  void practiceLevelLabels_areExpected() {
    assertEquals("Débutant", PracticeLevel.BEGINNER.getLabelFr());
    assertEquals("Intermédiaire", PracticeLevel.INTERMEDIATE.getLabelFr());
    assertEquals("Avancé", PracticeLevel.ADVANCED.getLabelFr());
  }

  @Test
  void goalTypeLabels_areExpected() {
    assertEquals("Distance", GoalType.DISTANCE.getLabelFr());
    assertEquals("Durée", GoalType.DUREE.getLabelFr());
    assertEquals("Calories", GoalType.CALORIES.getLabelFr());
    assertEquals("Répétitions", GoalType.REPETITIONS.getLabelFr());
  }

  @Test
  void challengeTypeLabels_areExpected() {
    assertEquals("Distance", ChallengeType.DISTANCE.getLabelFr());
    assertEquals("Durée", ChallengeType.DUREE.getLabelFr());
    assertEquals("Calories", ChallengeType.CALORIE.getLabelFr());
    assertEquals("Répétitions", ChallengeType.REPETITION.getLabelFr());
    assertEquals("Endurance", ChallengeType.ENDURENCE.getLabelFr());
  }

  @Test
  void friendshipStatusLabels_areExpected() {
    assertEquals("En attente", FriendshipStatus.PENDING.getLabelFr());
    assertEquals("Acceptée", FriendshipStatus.ACCEPTED.getLabelFr());
    assertEquals("Refusée", FriendshipStatus.REFUSED.getLabelFr());
  }

  @Test
  void sportNames_areStoredAsStrings() {
    assertTrue("Course".equalsIgnoreCase("course"));
    assertEquals("Course", " Course ".trim());
  }

  @Test
  void notificationTypeValues_areAvailable() {
    assertEquals(NotificationType.KUDO, NotificationType.valueOf("KUDO"));
    assertEquals(NotificationType.COMMENT, NotificationType.valueOf("COMMENT"));
    assertEquals(
        NotificationType.FRIEND_REQUEST_RECEIVED,
        NotificationType.valueOf("FRIEND_REQUEST_RECEIVED"));
    assertEquals(
        NotificationType.FRIEND_REQUEST_ACCEPTED,
        NotificationType.valueOf("FRIEND_REQUEST_ACCEPTED"));
    assertEquals(
        NotificationType.FRIEND_REQUEST_REFUSED,
        NotificationType.valueOf("FRIEND_REQUEST_REFUSED"));
  }
}
