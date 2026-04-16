package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.exercise.Exercise;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.notification.NotificationService;
import web.sportflow.sport.Sport;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.weather.WeatherService;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.statistique.MonthlyBarView;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

  @Mock private WorkoutRepository workoutRepository;
  @Mock private WeatherService weatherService;
  @Mock private FriendshipService friendshipService;
  @Mock private NotificationService notificationService;

  @InjectMocks private WorkoutService workoutService;

  private User user;
  private Sport running;

  @BeforeEach
  void initData() {
    user = new User("Alice", "Martin", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    user.setId(1L);
    user.setRole(Role.USER);

    running = new Sport("Course", 10.0);
    running.setId(11L);
  }

  @Test
  void createWorkout_fetchesWeatherWhenAddressAndDateAreValid() {
    Workout input = new Workout();
    input.setName("Morning run");
    input.setDate(LocalDateTime.now().withNano(0));
    input.setAddress("Toulouse");
    input.setDurationSec(1800.0);
    input.setSport(running);
    input.setUser(user);

    WeatherStatsDTO stats = new WeatherStatsDTO("20", "21", "18", "19", "0.0", "10", "clearsky");
    when(weatherService.getWeatherStats(input.getAddress(), input.getDate(), 1800.0))
        .thenReturn(stats);
    when(workoutRepository.save(any(Workout.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Workout created = workoutService.createWorkout(input);

    assertEquals(stats, created.getWeather());
    verify(weatherService).getWeatherStats(input.getAddress(), input.getDate(), 1800.0);
    verify(workoutRepository, times(2)).save(any(Workout.class));
  }

  @Test
  void createWorkout_skipsWeatherWhenAddressOrDateMissing() {
    Workout input = new Workout();
    input.setName("Session");
    input.setAddress("");
    input.setDate(null);
    input.setSport(running);
    input.setUser(user);

    when(workoutRepository.save(any(Workout.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    workoutService.createWorkout(input);

    verify(weatherService, never()).getWeatherStats(any(), any(), any());
  }

  @Test
  void listMethods_delegateAndGuardOnEmptyIds() {
    Workout workout = workoutWithDistance(1000.0, 600.0);
    when(workoutRepository.findAllByOrderByDateDesc()).thenReturn(List.of(workout));
    when(friendshipService.getCurrentUserAndFriendIds(1L)).thenReturn(List.of(1L, 2L));
    when(workoutRepository.findByUserIdInOrderByDateDesc(List.of(1L, 2L)))
        .thenReturn(List.of(workout));

    assertEquals(1, workoutService.getAll().size());
    assertEquals(1, workoutService.getFriendsWorkout(1L).size());
    assertTrue(workoutService.getAllForUsers(null).isEmpty());
    assertTrue(workoutService.getAllForUsers(List.of()).isEmpty());
    assertEquals(1, workoutService.getAllForUsers(List.of(1L, 2L)).size());
    assertEquals(1, workoutService.getAllStatutsForUser().size());
  }

  @Test
  void totalsAndCurves_areComputedFromRepositoryResults() {
    Workout withDistance = workoutWithDistance(1000.0, 600.0);
    Workout withoutDuration = workoutWithDistance(500.0, null);
    when(workoutRepository.findByUserAndDateBetween(any(), any(), any()))
        .thenReturn(List.of(withDistance, withoutDuration));

    assertEquals(1.0, workoutService.getTotalDistanceThisWeek(user), 0.0001);
    assertEquals(1.0, workoutService.getTotalDistanceThisMonth(user), 0.0001);
    assertEquals(1.0, workoutService.getTotalDistanceThisYear(user), 0.0001);
    assertEquals(600.0, workoutService.getTotalDurationThisWeek(user), 0.0001);
    assertTrue(workoutService.getTotalCaloriesThisWeek(user) > 0.0);

    List<Double> monthly = workoutService.getMonthlyDistancesCurrentYear(user);
    assertEquals(12, monthly.size());

    List<MonthlyBarView> bars = workoutService.getMonthlyBarViewsCurrentYear(user);
    assertEquals(12, bars.size());
    assertTrue(bars.stream().allMatch(bar -> bar.getHeight() >= 24));

    assertTrue(workoutService.getAverageMonthlyDistanceThisYear(user) > 0.0);
    assertTrue(workoutService.getDistanceGapVsAverageMonthly(user) >= -100.0);
    assertEquals(0, workoutService.getMonthlyProgressPercent(user, 0));
    assertEquals(100, workoutService.getMonthlyProgressPercent(user, 0.1));

    assertEquals(List.of("S1", "S2", "S3", "S4"), workoutService.getMonthDayLabels());
    assertEquals(4, workoutService.getCurrentMonthCurve(user).size());
    assertEquals(4, workoutService.getYearAverageCurve(user).size());

    assertEquals(
        List.of("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"), workoutService.getWeekLabels());
    assertEquals(7, workoutService.getWeekDistances(user).size());

    assertFalse(workoutService.getMonthLabelsForChart().isEmpty());
    assertFalse(workoutService.getMonthDistancesForChart(user).isEmpty());
    assertEquals(12, workoutService.getYearLabelsForChart().size());
    assertEquals(monthly, workoutService.getYearDistancesForChart(user));
  }

  @Test
  void findById_toggleKudo_saveAndDeleteCoverRemainingFlows() {
    Workout workout = workoutWithDistance(1000.0, 600.0);
    workout.setId(99L);
    when(workoutRepository.findById(99L)).thenReturn(Optional.of(workout));
    when(workoutRepository.save(any(Workout.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    assertTrue(workoutService.findById(99L).isPresent());

    workoutService.toggleKudo(99L, user);
    assertTrue(workout.isKudoedBy(user));
    verify(notificationService).notifyKudoOnWorkout(workout, user);

    workoutService.toggleKudo(99L, user);
    assertFalse(workout.isKudoedBy(user));

    workoutService.saveWorkout(workout, user);
    verify(workoutRepository, times(3)).save(any(Workout.class));

    workoutService.deleteWorkout(workout);
    verify(workoutRepository).delete(workout);
  }

  private Workout workoutWithDistance(Double distanceMeters, Double durationSec) {
    Workout workout = new Workout();
    workout.setName("Workout");
    workout.setDescription("Description");
    workout.setAddress("Toulouse");
    workout.setDate(LocalDateTime.now());
    workout.setRating(4.0);
    workout.setPublished(true);
    workout.setSport(running);
    workout.setUser(user);
    workout.setDurationSec(durationSec);

    WorkoutExercise exercise = new WorkoutExercise();
    exercise.setExercise(new Exercise("Course continue", 0.15));
    exercise.setDistanceM(distanceMeters);
    exercise.setDurationSec(durationSec == null ? 300.0 : durationSec);
    workout.setWorkoutExercises(List.of(exercise));

    return workout;
  }
}
