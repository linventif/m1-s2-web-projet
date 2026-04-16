package web.sportflow.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import web.sportflow.badge.Badge;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.exercise.Exercise;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalType;
import web.sportflow.notification.Notification;
import web.sportflow.notification.NotificationType;
import web.sportflow.sport.Sport;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutDashboardDisplay;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.comment.Comment;

class DomainModelCoverageTest {

  @Test
  void badgeGoalExerciseAndComment_coverCoreBranches() {
    Badge badge = new Badge("Rookie", "5km", "");
    assertEquals("/images/badge/running_5km.png", badge.getIconPath());
    badge.setIconPath("/badge_upload/custom.png");
    assertEquals("/badge_upload/custom.png", badge.getIconPath());

    Goal goal = new Goal("Obj", GoalType.DISTANCE, 50.0, 25.0, "km", null);
    assertEquals(50, goal.getProgressPercent());
    goal.setTargetValue(0.0);
    assertEquals(0, goal.getProgressPercent());

    Exercise exercise = new Exercise();
    exercise.setCaloriesPerSec(1.2);
    assertEquals(0.02, exercise.getCaloriesPerMin(), 0.0001);
    exercise.setCaloriesPerMin(0.5);
    assertEquals(30.0, exercise.getCaloriesPerSec(), 0.0001);

    Workout workout = new Workout();
    User author = user(1L, Role.USER);
    Comment comment = new Comment("Great", workout, author);
    assertEquals("Great", comment.getContent());
    comment.setContent("Updated");
    comment.setAuthor(author);
    comment.setWorkout(workout);
    assertEquals("Updated", comment.getContent());
  }

  @Test
  void notificationAndWorkoutExercise_coverLifecycleAndConversions() {
    User recipient = user(2L, Role.USER);
    Notification notification =
        new Notification(recipient, null, NotificationType.KUDO, "message", "/users/workout");
    notification.setCreatedAt(null);
    notification.prePersist();
    assertNotNull(notification.getCreatedAt());
    notification.setRead(true);
    assertTrue(notification.isRead());

    WorkoutExercise exercise = new WorkoutExercise();
    exercise.setWeightG(12.5);
    assertEquals(12.5, exercise.getWeightKg(), 0.0001);
    exercise.setDurationMin(2.0);
    assertEquals(120.0, exercise.getDurationSec(), 0.0001);
    exercise.setDurationSec(null);
    assertNull(exercise.getDurationMin());
    exercise.setAverageBpm(145.0);
    assertEquals(145.0, exercise.getAverageBpm());
  }

  @Test
  void sportUserWorkoutAndDashboard_coverMetricBranches() {
    Sport running = new Sport("Course", 10.0);
    Sport strength = new Sport("Musculation", 6.0);
    Sport mobility = new Sport("Yoga", 3.5);

    assertEquals("Course", running.getDisplayName());
    assertEquals("Musculation", strength.getDisplayName());
    assertEquals("Yoga", mobility.getDisplayName());
    assertThrows(IllegalArgumentException.class, () -> running.setMET(30.0));

    User athlete = user(3L, Role.USER);
    assertEquals("/images/avatars/user_0.png", athlete.getProfileImagePath());
    athlete.setProfileImagePath("/avatar_upload/user_3.png");
    assertEquals("/avatar_upload/user_3.png", athlete.getProfileImagePath());
    assertEquals("alice@demo.local", athlete.getUsername());
    assertEquals(1, athlete.getAuthorities().size());
    assertTrue(athlete.isAccountNonExpired());
    assertTrue(athlete.isAccountNonLocked());
    assertTrue(athlete.isCredentialsNonExpired());
    assertTrue(athlete.isEnabled());
    assertTrue(athlete.toString().contains("User{"));

    Workout distanceWorkout = new Workout();
    distanceWorkout.setSport(running);
    distanceWorkout.setUser(athlete);
    distanceWorkout.setDurationSec(1800.0);

    WorkoutExercise runPart = new WorkoutExercise();
    runPart.setDistanceM(5000.0);
    runPart.setDurationSec(1800.0);

    distanceWorkout.setWorkoutExercises(List.of(runPart));
    distanceWorkout.addKudo(athlete);
    assertTrue(distanceWorkout.isKudoedBy(athlete));
    assertEquals(1, distanceWorkout.getKudosCount());
    assertTrue(distanceWorkout.getOthersWhoKudoed(athlete).isEmpty());
    distanceWorkout.removeKudo(athlete);
    assertFalse(distanceWorkout.isKudoedBy(athlete));

    WorkoutDashboardDisplay distanceDisplay = new WorkoutDashboardDisplay(distanceWorkout);
    assertEquals("Distance", distanceDisplay.getPrimaryMetricLabel());
    assertTrue(distanceDisplay.getPrimaryMetricValue().contains("km"));
    assertTrue(
        distanceDisplay.getSecondaryMetricLabel().contains("Allure")
            || distanceDisplay.getSecondaryMetricLabel().contains("Vitesse"));

    Workout strengthWorkout = new Workout();
    strengthWorkout.setSport(strength);
    strengthWorkout.setUser(athlete);
    WorkoutExercise setExercise = new WorkoutExercise();
    setExercise.setSets(4);
    setExercise.setReps(10);
    setExercise.setWeightG(80000.0);
    setExercise.setDurationSec(900.0);
    strengthWorkout.setWorkoutExercises(List.of(setExercise));
    WorkoutDashboardDisplay strengthDisplay = new WorkoutDashboardDisplay(strengthWorkout);
    assertEquals("Séries", strengthDisplay.getPrimaryMetricLabel());
    assertTrue(
        strengthDisplay.getSecondaryMetricValue().contains("kg")
            || strengthDisplay.getSecondaryMetricValue().contains("répétitions"));

    Workout mobilityWorkout = new Workout();
    mobilityWorkout.setSport(mobility);
    mobilityWorkout.setUser(athlete);
    mobilityWorkout.setDurationSec(0.0);
    mobilityWorkout.setWorkoutExercises(new ArrayList<>());
    WorkoutDashboardDisplay mobilityDisplay = new WorkoutDashboardDisplay(mobilityWorkout);
    assertEquals("Aucune donnée", mobilityDisplay.getPrimaryMetricValue());

    Workout withComment = new Workout();
    Comment comment = new Comment("Nice", withComment, athlete);
    withComment.addComment(comment);
    assertEquals(withComment, comment.getWorkout());

    Workout defaultWorkout = new Workout();
    ReflectionTestUtils.invokeMethod(defaultWorkout, "applyDefaults");
    assertNotNull(defaultWorkout.getDate());
    assertNotNull(defaultWorkout.getName());

    Workout noSport = new Workout();
    noSport.setUser(athlete);
    noSport.setDurationSec(600.0);
    assertEquals(0.0, noSport.getCalorieBurn());
  }

  @Test
  void workoutCaloriesAndChallengeTimeWindows_coverRemainingBranches() {
    User athlete = user(4L, Role.USER);
    Sport running = new Sport("Course", 10.0);
    running.setId(44L);

    Workout workout = new Workout();
    workout.setSport(running);
    workout.setUser(athlete);
    workout.setDurationSec(1200.0);

    WorkoutExercise valid = new WorkoutExercise();
    valid.setDurationSec(600.0);
    valid.setWeightG(10000.0);

    WorkoutExercise ignored = new WorkoutExercise();
    ignored.setDurationSec(-100.0);

    workout.setWorkoutExercises(List.of(valid, ignored));
    assertTrue(workout.getCalories() > 0);
    assertTrue(workout.getCalorieBurn() > 0);

    Workout copy = new Workout();
    copy.setWorkoutExercises(new ArrayList<>(java.util.Arrays.asList(valid, null)));
    assertEquals(1, copy.getWorkoutExercises().size());
    copy.setDurationMin(null);
    assertNull(copy.getDurationSec());

    Challenge challenge =
        new Challenge(
            "Défi",
            "Desc",
            ChallengeType.DISTANCE,
            20.0,
            LocalDate.now().minusDays(2),
            LocalDate.now().plusDays(3),
            athlete,
            true);
    challenge.setSports(List.of(running));
    challenge.setBadges(List.of(new Badge("B", "D")));
    challenge.setParticipants(List.of(athlete));

    assertTrue(challenge.hasSportId(running.getId()));
    assertFalse(challenge.hasSportId(null));
    assertFalse(challenge.hasBadgeId(999L));
    assertTrue(challenge.hasParticipantId(athlete.getId()));
    assertTrue(challenge.isActive());
    assertTrue(challenge.getRemainingTimePercent() >= 0);
    assertTrue(challenge.getRemainingDays() >= 0);
    assertTrue(challenge.getStartDateShortUs().contains("/"));
    assertTrue(challenge.getEndDateShortUs().contains("/"));
    assertTrue(challenge.getSportNames().contains("Course"));
    assertTrue(challenge.getBadgeNames().contains("B"));
    assertTrue(challenge.getParticipantNames().contains("Alice Martin"));

    Challenge invalidDates =
        new Challenge(
            "Invalid",
            "",
            ChallengeType.DUREE,
            10.0,
            LocalDate.now(),
            LocalDate.now().minusDays(1),
            athlete,
            false);
    assertEquals(0, invalidDates.getRemainingTimePercent());
    assertEquals(0, invalidDates.getRemainingDays());

    Challenge sameDay =
        new Challenge(
            "Same day",
            "",
            ChallengeType.DUREE,
            10.0,
            LocalDate.now(),
            LocalDate.now(),
            athlete,
            false);
    int percent = sameDay.getRemainingTimePercent();
    assertTrue(percent == 0 || percent == 100);
  }

  private User user(Long id, Role role) {
    User user =
        new User(
            "Alice",
            "Martin",
            "alice@demo.local",
            "secret",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(1995, 1, 1),
            PracticeLevel.INTERMEDIATE);
    user.setId(id);
    user.setRole(role);
    return user;
  }
}
