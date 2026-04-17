package web.sportflow.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.badge.Badge;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.sport.Sport;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceAdditionalTest {

  @Mock private ChallengeRepository challengeRepository;
  @Mock private FriendshipService friendshipService;
  @Mock private UserRepository userRepository;
  @Mock private WorkoutRepository workoutRepository;

  @InjectMocks private ChallengeService challengeService;

  @Test
  void createChallenge_appliesOfficialRulesOnParticipantsAndBadges() {
    User creator = user(1L);

    Challenge official =
        new Challenge(
            "Official",
            "desc",
            ChallengeType.DISTANCE,
            10.0,
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            creator,
            true);
    official.setParticipants(List.of(user(2L)));
    official.setBadges(List.of(badge(99L)));

    when(challengeRepository.save(any(Challenge.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Challenge createdOfficial = challengeService.createChallenge(official);
    assertTrue(createdOfficial.getParticipants().isEmpty());
    assertEquals(1, createdOfficial.getBadges().size());

    Challenge community =
        new Challenge(
            "Community",
            "desc",
            ChallengeType.DUREE,
            120.0,
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            creator,
            false);
    community.setParticipants(List.of(user(2L)));
    community.setBadges(List.of(badge(100L)));

    Challenge createdCommunity = challengeService.createChallenge(community);
    assertEquals(1, createdCommunity.getParticipants().size());
    assertTrue(createdCommunity.getBadges().isEmpty());
  }

  @Test
  void searchAndCategorization_coverFilteringAndSortingBranches() {
    User creator = user(1L);
    Challenge officialTimed =
        challenge(
            1L,
            "Run April",
            ChallengeType.DISTANCE,
            true,
            creator,
            LocalDate.now(),
            LocalDate.now().plusDays(2));
    Challenge community =
        challenge(2L, "Yoga Crew", ChallengeType.DUREE, false, creator, null, null);

    when(challengeRepository.findAll()).thenReturn(List.of(community, officialTimed));

    List<Challenge> runSearch = challengeService.searchChallenges("officiel");
    assertEquals(1, runSearch.size());
    assertEquals(officialTimed, runSearch.getFirst());

    List<Challenge> all = challengeService.searchChallenges(" ");
    assertEquals(2, all.size());

    List<Challenge> officials =
        challengeService.getOfficialChallenges(List.of(community, officialTimed));
    assertEquals(1, officials.size());
    assertEquals(officialTimed, officials.getFirst());

    List<Challenge> communities =
        challengeService.getCommunityChallenges(List.of(community, officialTimed));
    assertEquals(1, communities.size());
    assertEquals(community, communities.getFirst());

    assertTrue(challengeService.getOfficialChallenges(null).isEmpty());
    assertTrue(challengeService.getCommunityChallenges(List.of()).isEmpty());
  }

  @Test
  void joinAndLeave_enforceRulesAndMembership() {
    User current = user(3L);
    User creator = user(1L);

    Challenge official =
        challenge(
            20L,
            "Official",
            ChallengeType.DISTANCE,
            true,
            creator,
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    Challenge community =
        challenge(
            21L,
            "Community",
            ChallengeType.DISTANCE,
            false,
            creator,
            LocalDate.now(),
            LocalDate.now().plusDays(1));
    community.setParticipants(new java.util.ArrayList<>());

    when(challengeRepository.findById(20L)).thenReturn(Optional.of(official));
    when(challengeRepository.findById(21L)).thenReturn(Optional.of(community));
    when(userRepository.findById(3L)).thenReturn(Optional.of(current));

    assertThrows(
        IllegalArgumentException.class, () -> challengeService.joinChallenge(20L, current));

    challengeService.joinChallenge(21L, current);
    challengeService.joinChallenge(21L, current);
    assertEquals(1, community.getParticipants().size());

    challengeService.leaveChallenge(21L, current);
    assertTrue(community.getParticipants().isEmpty());

    assertThrows(
        IllegalArgumentException.class, () -> challengeService.leaveChallenge(20L, current));
    assertThrows(
        IllegalArgumentException.class, () -> challengeService.leaveChallenge(21L, current));

    Challenge ended =
        challenge(
            22L,
            "Ended",
            ChallengeType.DUREE,
            false,
            creator,
            LocalDate.now().minusDays(3),
            LocalDate.now().minusDays(1));
    ended.setParticipants(new java.util.ArrayList<>(List.of(current)));
    when(challengeRepository.findById(22L)).thenReturn(Optional.of(ended));

    assertThrows(
        IllegalArgumentException.class, () -> challengeService.joinChallenge(22L, current));
    assertThrows(
        IllegalArgumentException.class, () -> challengeService.leaveChallenge(22L, current));
  }

  @Test
  void progressAndBadgeSync_coverComputationBranches() {
    User current = user(10L);
    User creator = user(1L);
    Sport running = new Sport("Course", 10.0);
    running.setId(100L);

    Badge badge = badge(501L);

    Challenge challenge =
        challenge(
            70L,
            "Distance",
            ChallengeType.DISTANCE,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));
    challenge.setSports(List.of(running));
    challenge.setBadges(new java.util.ArrayList<>(List.of(badge)));

    Workout workout = new Workout();
    workout.setDate(LocalDateTime.now());
    workout.setSport(running);
    workout.setUser(current);
    WorkoutExercise exercise = new WorkoutExercise();
    exercise.setDistanceM(5000.0);
    workout.setWorkoutExercises(List.of(exercise));

    when(userRepository.findById(10L)).thenReturn(Optional.of(current));
    when(workoutRepository.findByUserAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(workout));
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Map<Long, ChallengeDto> progressByChallenge =
        challengeService.buildProgressByChallenge(List.of(challenge), current);

    assertTrue(progressByChallenge.containsKey(70L));
    ChallengeDto progress = progressByChallenge.get(70L);
    assertTrue(progress.currentValue() >= 5.0);
    assertTrue(progress.percentage() > 0);
    assertEquals("km", progress.unitLabel());

    Set<Long> unlocked = challengeService.syncChallengeBadgesForUser(List.of(challenge), current);
    assertTrue(unlocked.contains(501L));

    Set<Long> unlockedSecondPass =
        challengeService.syncChallengeBadgesForUser(List.of(challenge), current);
    assertTrue(unlockedSecondPass.isEmpty());

    verify(userRepository).save(current);

    assertTrue(challengeService.buildProgressByChallenge(List.of(), current).isEmpty());
    assertTrue(challengeService.buildProgressByChallenge(List.of(challenge), null).isEmpty());

    assertTrue(challengeService.syncChallengeBadgesForUser(List.of(), current).isEmpty());
    verify(userRepository, never()).save(user(99L));
  }

  @Test
  void friendsAndUserChallenge_filtersByOfficialOrVisibleCreator() {
    User current = user(2L);
    User visibleFriend = user(3L);
    User hiddenCreator = user(4L);

    Challenge official =
        challenge(1L, "Off", ChallengeType.DISTANCE, true, hiddenCreator, null, null);
    Challenge friendChallenge =
        challenge(2L, "Friend", ChallengeType.DUREE, false, visibleFriend, null, null);
    Challenge hiddenChallenge =
        challenge(3L, "Hidden", ChallengeType.CALORIE, false, hiddenCreator, null, null);

    when(challengeRepository.findAll())
        .thenReturn(List.of(official, friendChallenge, hiddenChallenge));
    when(friendshipService.getCurrentUserAndFriend(current))
        .thenReturn(List.of(current, visibleFriend));

    List<Challenge> visible = challengeService.getFriendsAndUserChallenge(current);
    assertEquals(2, visible.size());
    assertTrue(visible.contains(official));
    assertTrue(visible.contains(friendChallenge));
  }

  @Test
  void buildProgressByChallenge_coversTypeSwitchAndWindowFilteringBranches() {
    User current = user(10L);
    User creator = user(1L);
    Sport running = new Sport("Course", 10.0);
    running.setId(100L);

    Workout workout = new Workout();
    workout.setDate(LocalDateTime.now());
    workout.setSport(running);
    workout.setUser(current);
    workout.setDurationSec(1800.0);
    WorkoutExercise exercise = new WorkoutExercise();
    exercise.setDistanceM(2000.0);
    exercise.setReps(12);
    exercise.setSets(3);
    workout.setWorkoutExercises(List.of(exercise));

    Workout noRepetitionWorkout = new Workout();
    noRepetitionWorkout.setDate(LocalDateTime.now());
    noRepetitionWorkout.setSport(running);
    noRepetitionWorkout.setUser(current);
    noRepetitionWorkout.setWorkoutExercises(List.of(new WorkoutExercise()));

    Challenge distance =
        challenge(
            101L,
            "Distance",
            ChallengeType.DISTANCE,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));
    distance.setSports(List.of(running));

    Challenge duration =
        challenge(
            102L,
            "Duration",
            ChallengeType.DUREE,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

    Challenge calorie =
        challenge(
            103L,
            "Calorie",
            ChallengeType.CALORIE,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

    Challenge repetition =
        challenge(
            104L,
            "Repetition",
            ChallengeType.REPETITION,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

    Challenge endurance =
        challenge(
            105L,
            "Endurance",
            ChallengeType.ENDURENCE,
            true,
            creator,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

    Challenge reversedWindow =
        challenge(
            106L,
            "Reversed",
            ChallengeType.DISTANCE,
            true,
            creator,
            LocalDate.now().plusDays(2),
            LocalDate.now().minusDays(2));

    when(userRepository.findById(10L)).thenReturn(Optional.of(current));
    when(workoutRepository.findByUserAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(workout, noRepetitionWorkout));

    Map<Long, ChallengeDto> progress =
        challengeService.buildProgressByChallenge(
            List.of(distance, duration, calorie, repetition, endurance, reversedWindow), current);

    assertEquals("km", progress.get(101L).unitLabel());
    assertEquals("min", progress.get(102L).unitLabel());
    assertEquals("kcal", progress.get(103L).unitLabel());
    assertEquals("reps", progress.get(104L).unitLabel());
    assertEquals("min", progress.get(105L).unitLabel());
    assertEquals(0.0, progress.get(106L).currentValue());

    assertTrue(progress.get(101L).currentValue() > 0.0);
    assertTrue(progress.get(104L).currentValue() > 0.0);
  }

  private Challenge challenge(
      Long id,
      String title,
      ChallengeType type,
      boolean official,
      User creator,
      LocalDate start,
      LocalDate end) {
    Challenge challenge = new Challenge(title, "desc", type, 5.0, start, end, creator, official);
    challenge.setId(id);
    challenge.setSports(new java.util.ArrayList<>());
    challenge.setBadges(new java.util.ArrayList<>());
    challenge.setParticipants(new java.util.ArrayList<>());
    return challenge;
  }

  private Badge badge(Long id) {
    Badge badge = new Badge("badge" + id, "desc");
    badge.setId(id);
    return badge;
  }

  private User user(Long id) {
    User user = new User("U" + id, "Test", "u" + id + "@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
