package web.sportflow.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeRepository;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseRepository;
import web.sportflow.friendship.Friendship;
import web.sportflow.friendship.FriendshipRepository;
import web.sportflow.friendship.FriendshipStatus;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalRepository;
import web.sportflow.goal.GoalType;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportRepository;
import web.sportflow.sport.SportService;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.WorkoutExerciseRepository;
import web.sportflow.workout.WorkoutRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminControllerCoverageTest {

  @Mock private UserRepository userRepository;
  @Mock private SportRepository sportRepository;
  @Mock private WorkoutRepository workoutRepository;
  @Mock private WorkoutExerciseRepository workoutExerciseRepository;
  @Mock private ExerciseRepository exerciseRepository;
  @Mock private SportService sportService;
  @Mock private BadgeRepository badgeRepository;
  @Mock private GoalRepository goalRepository;
  @Mock private ChallengeRepository challengeRepository;
  @Mock private FriendshipRepository friendshipRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private AdminController controller;

  private User user;
  private Sport sport;
  private Badge badge;
  private Goal goal;
  private Workout workout;
  private Challenge challenge;
  private Friendship friendship;

  @BeforeEach
  void setUp() {
    controller =
        new AdminController(
            userRepository,
            sportRepository,
            workoutRepository,
            workoutExerciseRepository,
            exerciseRepository,
            sportService,
            badgeRepository,
            goalRepository,
            challengeRepository,
            friendshipRepository,
            passwordEncoder);

    user = new User("Alice", "Martin", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    user.setId(1L);
    user.setRole(Role.USER);

    sport = new Sport("Course", 9.5);
    sport.setId(10L);

    badge = new Badge("Course - Starter", "desc", "/images/badge/running_5km.png");
    badge.setId(20L);

    goal = new Goal("Run", GoalType.DISTANCE, 10.0, 3.0, "km", user);
    goal.setId(30L);

    workout = new Workout();
    workout.setId(40L);
    workout.setUser(user);
    workout.setSport(sport);

    challenge =
        new Challenge(
            "Defi",
            "desc",
            ChallengeType.DISTANCE,
            10.0,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            user,
            true);
    challenge.setId(50L);

    friendship = new Friendship(user, user(2L), FriendshipStatus.PENDING);
    friendship.setId(60L);

    when(passwordEncoder.encode(any())).thenReturn("encoded");

    when(userRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(user)));
    when(userRepository.findAll()).thenReturn(new ArrayList<>(List.of(user)));
    when(userRepository.findAllById(anyList())).thenReturn(new ArrayList<>(List.of(user)));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
    when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(sportRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(sport)));
    when(sportRepository.findAll()).thenReturn(new ArrayList<>(List.of(sport)));
    when(sportRepository.findById(10L)).thenReturn(Optional.of(sport));
    when(sportRepository.findAllById(anyList())).thenReturn(new ArrayList<>(List.of(sport)));
    when(sportRepository.save(any(Sport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(workoutRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(workout)));
    when(workoutRepository.findAll()).thenReturn(new ArrayList<>(List.of(workout)));
    when(workoutRepository.findById(40L)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(workoutExerciseRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(new WorkoutExercise())));
    when(workoutExerciseRepository.findById(any(Long.class)))
        .thenReturn(Optional.of(new WorkoutExercise()));

    when(exerciseRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(new Exercise())));

    when(badgeRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(badge)));
    when(badgeRepository.findById(20L)).thenReturn(Optional.of(badge));
    when(badgeRepository.findAllById(anyList())).thenReturn(new ArrayList<>(List.of(badge)));
    when(badgeRepository.findByName(any())).thenReturn(Optional.empty());
    when(badgeRepository.save(any(Badge.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(goalRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(goal)));
    when(goalRepository.findAll()).thenReturn(new ArrayList<>(List.of(goal)));
    when(goalRepository.findById(30L)).thenReturn(Optional.of(goal));
    when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(challengeRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(challenge)));
    when(challengeRepository.findAll()).thenReturn(new ArrayList<>(List.of(challenge)));
    when(challengeRepository.findById(50L)).thenReturn(Optional.of(challenge));
    when(challengeRepository.save(any(Challenge.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(friendshipRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(new ArrayList<>(List.of(friendship)));
    when(friendshipRepository.findAll()).thenReturn(new ArrayList<>(List.of(friendship)));
    when(friendshipRepository.findById(60L)).thenReturn(Optional.of(friendship));
    when(friendshipRepository.findRelationshipBetween(any(Long.class), any(Long.class)))
        .thenReturn(Optional.empty());
    when(friendshipRepository.save(any(Friendship.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void viewEndpoints_returnExpectedTemplates() {
    Model model = new ExtendedModelMap();
    assertEquals("admin-index", controller.showPanel(model));
    assertEquals("admin-users", controller.showUsersPage(new ExtendedModelMap()));
    assertEquals("admin-sports", controller.showSportsPage(new ExtendedModelMap()));
    assertEquals("admin-workouts", controller.showWorkoutsPage(new ExtendedModelMap()));
    assertEquals("admin-badges", controller.showBadgesPage(new ExtendedModelMap()));
    assertEquals("admin-goals", controller.showGoalsPage(new ExtendedModelMap()));
    assertEquals("admin-challenges", controller.showChallengesPage(new ExtendedModelMap()));
    assertEquals("admin-friendships", controller.showFriendshipsPage(new ExtendedModelMap()));
  }

  @Test
  void userAndSportCrudEndpoints_coverSuccessAndErrorBranches() {
    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

    assertEquals(
        "redirect:/admin/users",
        controller.createUser(
            "Alice",
            "Martin",
            "alice.new@demo.local",
            "pwd",
            Role.USER,
            60.0,
            165.0,
            Sex.FEMALE,
            PracticeLevel.INTERMEDIATE,
            LocalDate.of(1995, 1, 1),
            null,
            List.of(10L),
            List.of(20L),
            redirect));

    when(userRepository.existsByEmail("taken@demo.local")).thenReturn(true);
    assertEquals(
        "redirect:/admin/users",
        controller.createUser(
            "A",
            "B",
            "taken@demo.local",
            "pwd",
            Role.USER,
            60.0,
            170.0,
            Sex.FEMALE,
            PracticeLevel.BEGINNER,
            null,
            null,
            null,
            null,
            new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/users",
        controller.updateUser(
            1L,
            "Alice",
            "Martin",
            "alice@demo.local",
            "",
            Role.ADMIN,
            58.0,
            166.0,
            Sex.FEMALE,
            PracticeLevel.ADVANCED,
            LocalDate.of(1995, 1, 1),
            "/avatar_upload/x.png",
            List.of(10L),
            List.of(20L),
            new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/users", controller.deleteUser(1L, user, new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/sports",
        controller.createSport("Musculation", 6.0, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/sports",
        controller.updateSport(10L, "Cyclisme", 7.0, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/sports", controller.deleteSport(10L, new RedirectAttributesModelMap()));
  }

  @Test
  void badgeGoalWorkoutChallengeFriendshipCrudEndpoints_coverMainFlows() {
    assertEquals(
        "redirect:/admin/badges",
        controller.createBadge("Badge 1", "Desc", null, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/badges",
        controller.updateBadge(
            20L, "Badge 2", "Desc2", "local.png", new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/badges", controller.deleteBadge(20L, new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/goals",
        controller.createGoal(
            "Goal", GoalType.DISTANCE, 10.0, 1.0, "km", 1L, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/goals",
        controller.updateGoal(
            30L,
            "Goal2",
            GoalType.CALORIES,
            20.0,
            5.0,
            "kcal",
            1L,
            new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/goals", controller.deleteGoal(30L, new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/workouts",
        controller.createWorkout(
            "Workout",
            "Desc",
            LocalDateTime.now(),
            "Toulouse",
            "4.5",
            List.of(new WorkoutExercise()),
            10L,
            1L,
            new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/workouts",
        controller.updateWorkout(
            40L,
            "Workout2",
            "Desc2",
            LocalDateTime.now(),
            "Paris",
            "3.5",
            List.of(new WorkoutExercise()),
            10L,
            1L,
            new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/workouts",
        controller.deleteWorkout(40L, new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/challenges",
        controller.createChallenge(
            "Challenge",
            "Desc",
            ChallengeType.DISTANCE,
            10.0,
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            1L,
            true,
            List.of(10L),
            List.of(1L, 2L),
            List.of(20L),
            new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/challenges",
        controller.updateChallenge(
            50L,
            "Challenge2",
            "Desc2",
            ChallengeType.DUREE,
            60.0,
            LocalDate.now(),
            LocalDate.now().plusDays(2),
            1L,
            false,
            List.of(10L),
            List.of(1L),
            List.of(20L),
            new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/challenges",
        controller.deleteChallenge(50L, new RedirectAttributesModelMap()));

    assertEquals(
        "redirect:/admin/friendships",
        controller.createFriendship(
            1L, 2L, FriendshipStatus.PENDING, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/friendships",
        controller.updateFriendship(
            60L, 1L, 2L, FriendshipStatus.ACCEPTED, new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/admin/friendships",
        controller.deleteFriendship(60L, new RedirectAttributesModelMap()));
  }

  @Test
  void errorMessageBranch_forDataIntegrityViolation_isHandled() {
    when(badgeRepository.save(any(Badge.class)))
        .thenThrow(new DataIntegrityViolationException("db"));

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
    String view = controller.createBadge("B", "D", null, redirect);

    assertEquals("redirect:/admin/badges", view);
    assertEquals(
        "Operation refusee par la base de donnees. Verifiez les dependances.",
        redirect.getFlashAttributes().get("errorMessage"));
  }

  private User user(Long id) {
    User u = new User("User", "Test", "u" + id + "@demo.local", 70.0, 180.0, Sex.MALE);
    u.setId(id);
    u.setRole(Role.USER);
    return u;
  }
}
