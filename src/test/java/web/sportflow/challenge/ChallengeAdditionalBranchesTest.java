package web.sportflow.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import web.sportflow.badge.Badge;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportName;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class ChallengeAdditionalBranchesTest {

  @Test
  void challengeTimeBranches_coverEdgeCases() {
    User creator = new User("A", "B", "a@b.c", 60.0, 170.0, Sex.FEMALE);
    creator.setId(1L);
    creator.setRole(Role.USER);

    Challenge noDates = new Challenge("NoDate", "", ChallengeType.DUREE, 10.0, null, null, creator);
    assertEquals(0, noDates.getRemainingTimePercent());
    assertEquals(0, noDates.getRemainingDays());

    LocalDate today = LocalDate.now();
    Challenge future =
        new Challenge(
            "Future",
            "",
            ChallengeType.DISTANCE,
            10.0,
            today.plusDays(2),
            today.plusDays(5),
            creator);
    assertEquals(0, future.getRemainingTimePercent());
    assertTrue(future.getRemainingDays() > 0);

    Challenge past =
        new Challenge(
            "Past",
            "",
            ChallengeType.DISTANCE,
            10.0,
            today.minusDays(5),
            today.minusDays(1),
            creator);
    assertEquals(100, past.getRemainingTimePercent());
    assertEquals(0, past.getRemainingDays());

    Challenge singleFuture =
        new Challenge(
            "Single",
            "",
            ChallengeType.DISTANCE,
            10.0,
            today.plusDays(1),
            today.plusDays(1),
            creator);
    assertEquals(0, singleFuture.getRemainingTimePercent());

    Challenge singleNow =
        new Challenge("SingleNow", "", ChallengeType.DISTANCE, 10.0, today, today, creator);
    assertEquals(100, singleNow.getRemainingTimePercent());
  }

  @Test
  void challengeNames_coverNullEntries() {
    User creator = new User("A", "B", "a@b.c", 60.0, 170.0, Sex.FEMALE);
    creator.setId(1L);

    Challenge challenge =
        new Challenge(
            "Named",
            "",
            ChallengeType.DISTANCE,
            10.0,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            creator);

    Sport sport = new Sport(SportName.Course, 9.0);
    sport.setId(2L);
    Badge badge = new Badge("BadgeX", "desc");
    badge.setId(3L);

    challenge.setSports(new ArrayList<>(java.util.Arrays.asList(sport, null)));
    challenge.setBadges(new ArrayList<>(java.util.Arrays.asList(badge, null)));
    challenge.setParticipants(new ArrayList<>(java.util.Arrays.asList(creator, null)));

    assertTrue(challenge.getSportNames().contains("Course"));
    assertTrue(challenge.getBadgeNames().contains("BadgeX"));
    assertTrue(challenge.getParticipantNames().contains("A B"));
  }
}
