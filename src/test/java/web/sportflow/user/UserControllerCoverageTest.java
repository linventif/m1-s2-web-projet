package web.sportflow.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeService;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeProgress;
import web.sportflow.challenge.ChallengeService;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.friendship.Friendship;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.friendship.FriendshipStatus;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalService;
import web.sportflow.goal.GoalType;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportName;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutService;
import web.sportflow.workout.statistique.MonthlyBarView;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerCoverageTest {

  @Mock private UserService userService;
  @Mock private WorkoutService workoutService;
  @Mock private GoalService goalService;
  @Mock private ChallengeService challengeService;
  @Mock private BadgeService badgeService;
  @Mock private FriendshipService friendshipService;

  private UserController controller;
  private User currentUser;
  private User otherUser;
  private Badge badge;
  private Goal goal;
  private Sport sport;
  private Workout workout;
  private Challenge challenge;

  @BeforeEach
  void setUp() {
    controller =
        new UserController(
            userService,
            workoutService,
            goalService,
            challengeService,
            badgeService,
            friendshipService);

    currentUser = user(1L, "alice@demo.local");
    otherUser = user(2L, "bob@demo.local");

    badge = new Badge("Course - Rookie", "desc");
    badge.setId(10L);
    goal = new Goal("Run", GoalType.DISTANCE, 50.0, 10.0, "km", currentUser);
    goal.setId(20L);
    sport = new Sport(SportName.Course, 9.5);
    sport.setId(30L);

    currentUser.getBadges().add(badge);
    currentUser.getGoals().add(goal);
    currentUser.getSports().add(sport);

    workout = new Workout();
    workout.setId(100L);
    workout.setUser(currentUser);
    workout.setSport(sport);

    challenge =
        new Challenge(
            "Defi",
            "desc",
            ChallengeType.DISTANCE,
            5.0,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1),
            currentUser,
            false);
    challenge.setId(40L);
    challenge.setParticipants(new ArrayList<>(List.of(currentUser, otherUser)));
    challenge.setBadges(new ArrayList<>(List.of(badge)));

    when(userService.getUserById(1L)).thenReturn(Optional.of(currentUser));
    when(userService.getUserById(2L)).thenReturn(Optional.of(otherUser));
    when(userService.getAll(PageRequest.of(0, 10)))
        .thenReturn(new PageImpl<>(List.of(currentUser, otherUser)));
    when(userService.searchUsers(any(), any())).thenReturn(new PageImpl<>(List.of(otherUser)));
    when(userService.createUser(any(), any(), any(), any(), any())).thenReturn(currentUser);
    when(userService.registerUser(any(RegistrationDTO.class))).thenReturn(currentUser);
    when(userService.updateCurrentUserProfile(
            any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(currentUser);
    when(userService.calculateBMI(any())).thenReturn(22.0);
    when(userService.calculateBMR(any())).thenReturn(1650.0);
    when(userService.getWorkoutRecommendation(any())).thenReturn("Recommendation");

    when(workoutService.getAll()).thenReturn(List.of(workout));
    when(workoutService.getFriendsWorkout(anyLong())).thenReturn(List.of(workout));
    when(workoutService.getTotalDistanceThisWeek(any())).thenReturn(12.5);
    when(workoutService.getTotalDurationThisWeek(any())).thenReturn(3600.0);
    when(workoutService.getTotalCaloriesThisWeek(any())).thenReturn(700.0);
    when(workoutService.getTotalDistanceThisMonth(any())).thenReturn(42.0);
    when(workoutService.getTotalDistanceThisYear(any())).thenReturn(100.0);
    when(workoutService.getAverageMonthlyDistanceThisYear(any())).thenReturn(20.0);
    when(workoutService.getDistanceGapVsAverageMonthly(any())).thenReturn(2.0);
    when(workoutService.getMonthlyBarViewsCurrentYear(any()))
        .thenReturn(List.of(new MonthlyBarView("Jan", 1.0, 20, false)));
    when(workoutService.getMonthDayLabels()).thenReturn(List.of("S1", "S2", "S3", "S4"));
    when(workoutService.getCurrentMonthCurve(any())).thenReturn(List.of(1.0, 2.0, 3.0, 4.0));
    when(workoutService.getYearAverageCurve(any())).thenReturn(List.of(1.0, 1.0, 1.0, 1.0));
    when(workoutService.getWeekLabels()).thenReturn(List.of("Lun", "Mar"));
    when(workoutService.getWeekDistances(any())).thenReturn(List.of(1.0, 2.0));
    when(workoutService.getMonthLabelsForChart()).thenReturn(List.of("1", "2"));
    when(workoutService.getMonthDistancesForChart(any())).thenReturn(List.of(1.0, 2.0));
    when(workoutService.getYearLabelsForChart()).thenReturn(List.of("Jan"));
    when(workoutService.getYearDistancesForChart(any())).thenReturn(List.of(12.0));

    when(goalService.getFriendsAndUserGoal(any())).thenReturn(List.of(goal));

    when(challengeService.searchChallenges(any())).thenReturn(List.of(challenge));
    when(challengeService.getOfficialChallenges(any())).thenReturn(List.of());
    when(challengeService.getCommunityChallenges(any())).thenReturn(List.of(challenge));
    when(challengeService.buildProgressByChallenge(any(), any()))
        .thenReturn(Map.of(40L, new ChallengeProgress(5.0, 5.0, 100, true, "km")));
    when(challengeService.getFriendsAndUserChallenge(any())).thenReturn(List.of(challenge));
    when(challengeService.syncChallengeBadgesForUser(any(), any())).thenReturn(Set.of(10L));

    when(badgeService.getAll()).thenReturn(List.of(badge));

    Friendship incoming = new Friendship(otherUser, currentUser, FriendshipStatus.PENDING);
    incoming.setId(500L);
    Friendship accepted = new Friendship(currentUser, otherUser, FriendshipStatus.ACCEPTED);
    accepted.setId(501L);

    when(friendshipService.getIncomingPendingRequests(1L)).thenReturn(List.of(incoming));
    when(friendshipService.getOutgoingPendingRequests(1L)).thenReturn(List.of());
    when(friendshipService.getAcceptedFriendships(1L)).thenReturn(List.of(accepted));
    when(friendshipService.sendRequest(anyLong(), anyLong())).thenReturn(incoming);
    when(friendshipService.findRelationshipBetween(1L, 2L)).thenReturn(Optional.of(incoming));
  }

  @Test
  void basicPages_and_registration_flows_areHandled() {
    assertEquals("user-menu", controller.showMenu());

    Model createModel = new ExtendedModelMap();
    assertEquals("user-create", controller.showCreateForm(createModel));

    assertEquals(
        "user-list",
        controller.createUser(
            currentUser, "pwd", "", List.of(), List.of(), new ExtendedModelMap()));

    when(userService.createUser(any(), any(), any(), any(), any()))
        .thenThrow(new IllegalArgumentException("invalid"));
    assertEquals(
        "user-create",
        controller.createUser(
            currentUser, "pwd", "", List.of(), List.of(), new ExtendedModelMap()));

    assertEquals(
        "user-login",
        controller.registerUser(
            new RegistrationDTO("new@demo.local", "pwd", "N", "U", Sex.MALE, 70.0, 180.0),
            new ExtendedModelMap()));

    when(userService.registerUser(any(RegistrationDTO.class)))
        .thenThrow(new RuntimeException("oops"));
    assertEquals(
        "user-create",
        controller.registerUser(
            new RegistrationDTO("new@demo.local", "pwd", "N", "U", Sex.MALE, 70.0, 180.0),
            new ExtendedModelMap()));
  }

  @Test
  void profile_and_edit_flows_cover_success_redirect_and_error() {
    assertEquals("user-profile", controller.showProfile(currentUser, new ExtendedModelMap()));

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
    assertEquals(
        "redirect:/user/profile",
        controller.showUserProfile(currentUser, 1L, new ExtendedModelMap(), redirect));
    assertEquals(
        "user-profile",
        controller.showUserProfile(currentUser, 2L, new ExtendedModelMap(), redirect));

    when(userService.getUserById(999L)).thenReturn(Optional.empty());
    assertEquals(
        "redirect:/user/friends",
        controller.showUserProfile(
            currentUser, 999L, new ExtendedModelMap(), new RedirectAttributesModelMap()));

    assertEquals(
        "user-profile-edit", controller.showEditProfile(currentUser, new ExtendedModelMap()));

    MockMultipartFile emptyFile = new MockMultipartFile("avatarFile", new byte[0]);
    assertEquals(
        "redirect:/user/profile",
        controller.updateProfile(
            currentUser,
            "Alice",
            "Martin",
            "alice@demo.local",
            60.0,
            165.0,
            Sex.FEMALE,
            PracticeLevel.INTERMEDIATE,
            LocalDate.of(1995, 1, 1),
            emptyFile,
            new ExtendedModelMap(),
            new RedirectAttributesModelMap()));

    when(userService.updateCurrentUserProfile(
            any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenThrow(new IllegalArgumentException("invalid"));
    assertEquals(
        "user-profile-edit",
        controller.updateProfile(
            currentUser,
            "Alice",
            "Martin",
            "alice@demo.local",
            60.0,
            165.0,
            Sex.FEMALE,
            PracticeLevel.INTERMEDIATE,
            LocalDate.of(1995, 1, 1),
            emptyFile,
            new ExtendedModelMap(),
            new RedirectAttributesModelMap()));
  }

  @Test
  void friendship_and_challenge_actions_handle_messages_and_return_paths() {
    Model model = new ExtendedModelMap();
    assertEquals(
        "user-friends", controller.manageFriends(currentUser, "bob", PageRequest.of(0, 10), model));
    assertEquals(
        "user-friends",
        controller.manageFriends(currentUser, null, PageRequest.of(0, 10), new ExtendedModelMap()));

    assertEquals(
        "user-challenges", controller.showChallenges(currentUser, "defi", new ExtendedModelMap()));

    RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();
    assertEquals(
        "redirect:/users/challenges",
        controller.joinChallenge(currentUser, 40L, "/users/challenges", redirect));
    assertEquals(
        "redirect:/users/challenges",
        controller.leaveChallenge(currentUser, 40L, "/users/challenges", redirect));

    assertEquals(
        "redirect:/users/friends",
        controller.sendFriendRequest(
            currentUser, 2L, "https://evil", new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/users/friends",
        controller.acceptFriendRequest(
            currentUser, 500L, "/users/friends", new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/users/friends",
        controller.refuseFriendRequest(
            currentUser, 500L, "/users/friends", new RedirectAttributesModelMap()));
    assertEquals(
        "redirect:/users/friends",
        controller.unfriend(currentUser, 2L, "/users/friends", new RedirectAttributesModelMap()));
  }

  @Test
  void workout_dashboard_and_stats_views_are_populated() {
    assertEquals("user-workout", controller.showWorkout(new ExtendedModelMap()));
    assertEquals("redirect:/users/profile#goals", controller.redirectGoalsPage());
    assertEquals("dashboard", controller.showDashboard(currentUser, new ExtendedModelMap()));
    assertEquals(
        "user-statistique", controller.showStatistiquePage(currentUser, new ExtendedModelMap()));
    assertEquals("redirect:/users/friends", controller.redirectUsersPage());
  }

  private User user(Long id, String email) {
    User user = new User("User" + id, "Test", email, 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
