package web.sportflow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeRepository;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseRepository;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalRepository;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportRepository;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutRepository;

class ReferenceDataInitializerTest {

  @TempDir Path tempDir;

  @Test
  void run_seedsReferenceDataWithoutThrowing() {
    WorkoutRepository workoutRepository = Mockito.mock(WorkoutRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    SportRepository sportRepository = Mockito.mock(SportRepository.class);
    BadgeRepository badgeRepository = Mockito.mock(BadgeRepository.class);
    ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    GoalRepository goalRepository = Mockito.mock(GoalRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    FriendshipService friendshipService = Mockito.mock(FriendshipService.class);
    ExerciseRepository exerciseRepository = Mockito.mock(ExerciseRepository.class);

    when(passwordEncoder.encode("demo123")).thenReturn("encoded-demo123");

    when(userRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(sportRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(badgeRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(challengeRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(goalRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(exerciseRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));
    when(workoutRepository.saveAll(anyList()))
        .thenAnswer(invocation -> assignIds(invocation.getArgument(0)));

    ReferenceDataInitializer initializer =
        new ReferenceDataInitializer(
            userRepository,
            sportRepository,
            badgeRepository,
            challengeRepository,
            goalRepository,
            passwordEncoder,
            workoutRepository,
            friendshipService,
            exerciseRepository);

    ReflectionTestUtils.setField(
        initializer, "avatarUploadDir", tempDir.resolve("avatars").toString());
    ReflectionTestUtils.setField(
        initializer, "badgeUploadDir", tempDir.resolve("badges").toString());

    initializer.run();

    verify(userRepository, atLeastOnce()).saveAll(anyList());
    verify(sportRepository, atLeastOnce()).saveAll(anyList());
    verify(badgeRepository, atLeastOnce()).saveAll(anyList());
    verify(challengeRepository, atLeastOnce()).saveAll(anyList());
    verify(goalRepository, atLeastOnce()).saveAll(anyList());
    verify(exerciseRepository, atLeastOnce()).saveAll(anyList());
    verify(workoutRepository, atLeastOnce()).saveAll(anyList());
  }

  @Test
  void fileHelpers_extractAndCleanupPaths(@TempDir Path tmp) throws IOException {
    ReferenceDataInitializer initializer = minimalInitializer();

    String normal = ReflectionTestUtils.invokeMethod(initializer, "extractFileName", "/a/b/c.png");
    String withQuery =
        ReflectionTestUtils.invokeMethod(initializer, "extractFileName", "/a/b/c.png?x=1#f");
    String traversal =
        ReflectionTestUtils.invokeMethod(initializer, "extractFileName", "../secret");

    assertEquals("c.png", normal);
    assertEquals("c.png", withQuery);
    assertEquals("secret", traversal);

    assertEquals("png", ReflectionTestUtils.invokeMethod(initializer, "extractExtension", "a.png"));
    assertEquals("", ReflectionTestUtils.invokeMethod(initializer, "extractExtension", "a"));
    assertEquals("", ReflectionTestUtils.invokeMethod(initializer, "extractExtension", "a."));

    Path uploadDir = tmp.resolve("avatars");
    Files.createDirectories(uploadDir);
    Path avatar = uploadDir.resolve("user_42.png");
    Files.writeString(avatar, "x");
    Path other = uploadDir.resolve("other.png");
    Files.writeString(other, "y");

    ReflectionTestUtils.invokeMethod(initializer, "cleanupExistingUserAvatars", uploadDir, 42L);

    assertFalse(Files.exists(avatar));
    assertTrue(Files.exists(other));
  }

  @Test
  void assignDemoBadgeIcons_handlesNullAndEmptyEntries() {
    BadgeRepository badgeRepository = Mockito.mock(BadgeRepository.class);
    when(badgeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

    ReferenceDataInitializer initializer = minimalInitializerWithBadgeRepo(badgeRepository);
    ReflectionTestUtils.setField(
        initializer, "badgeUploadDir", tempDir.resolve("badge-assets").toString());

    List<Badge> badges = new ArrayList<>();
    badges.add(null);
    badges.add(new Badge("B1", "D1", ""));
    badges.add(new Badge("B2", "D2", "/images/badge/not-existing.png"));

    ReflectionTestUtils.invokeMethod(initializer, "assignDemoBadgeIcons", badges);

    verify(badgeRepository).saveAll(badges);
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> assignIds(List<T> entities) {
    long id = 1L;
    for (T entity : entities) {
      if (entity instanceof User user && user.getId() == null) {
        user.setId(id++);
      } else if (entity instanceof Sport sport && sport.getId() == null) {
        sport.setId(id++);
      } else if (entity instanceof Badge badge && badge.getId() == null) {
        badge.setId(id++);
      } else if (entity instanceof Challenge challenge && challenge.getId() == null) {
        challenge.setId(id++);
      } else if (entity instanceof Goal goal && goal.getId() == null) {
        goal.setId(id++);
      } else if (entity instanceof Exercise exercise && exercise.getId() == null) {
        exercise.setId(id++);
      } else if (entity instanceof Workout workout && workout.getId() == null) {
        workout.setId(id++);
      }
    }
    return entities;
  }

  private ReferenceDataInitializer minimalInitializer() {
    return minimalInitializerWithBadgeRepo(Mockito.mock(BadgeRepository.class));
  }

  private ReferenceDataInitializer minimalInitializerWithBadgeRepo(
      BadgeRepository badgeRepository) {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    SportRepository sportRepository = Mockito.mock(SportRepository.class);
    ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);
    GoalRepository goalRepository = Mockito.mock(GoalRepository.class);
    PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
    WorkoutRepository workoutRepository = Mockito.mock(WorkoutRepository.class);
    FriendshipService friendshipService = Mockito.mock(FriendshipService.class);
    ExerciseRepository exerciseRepository = Mockito.mock(ExerciseRepository.class);

    when(passwordEncoder.encode(any())).thenReturn("encoded");
    when(userRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(sportRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(challengeRepository.saveAll(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(goalRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(exerciseRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(workoutRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    return new ReferenceDataInitializer(
        userRepository,
        sportRepository,
        badgeRepository,
        challengeRepository,
        goalRepository,
        passwordEncoder,
        workoutRepository,
        friendshipService,
        exerciseRepository);
  }
}
