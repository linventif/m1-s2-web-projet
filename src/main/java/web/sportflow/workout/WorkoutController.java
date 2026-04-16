package web.sportflow.workout;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import web.sportflow.badge.Badge;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseService;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportService;
import web.sportflow.user.Role;
import web.sportflow.user.User;
import web.sportflow.workout.comment.CommentService;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

  private final WorkoutService workoutService;
  private final CommentService commentService;
  private final SportService sportService;
  private final ExerciseService exerciseService;

  public WorkoutController(
      WorkoutService workoutService,
      CommentService commentService,
      SportService sportService,
      ExerciseService exerciseService) {
    this.workoutService = workoutService;
    this.commentService = commentService;
    this.sportService = sportService;
    this.exerciseService = exerciseService;
  }

  @GetMapping({"", "/"})
  public String listWorkouts(Model model) {
    List<Workout> workouts = workoutService.getAll();
    Map<Long, List<Badge>> unlockedBadgesByWorkoutId = new HashMap<>();
    for (Workout workout : workouts) {
      unlockedBadgesByWorkoutId.put(workout.getId(), getUnlockedBadgesForWorkout(workout));
    }
    model.addAttribute("workouts", workouts);
    model.addAttribute(
        "workoutDisplays", workouts.stream().map(WorkoutDashboardDisplay::new).toList());
    model.addAttribute("unlockedBadgesByWorkoutId", unlockedBadgesByWorkoutId);
    return "user-workout";
  }

  @PostMapping("/{id}/kudo")
  @ResponseBody
  public Map<String, Object> toggleKudo(
      @PathVariable Long id, @AuthenticationPrincipal User currentUser) {
    workoutService.toggleKudo(id, currentUser);
    Workout workout = workoutService.findById(id).orElseThrow();
    boolean isKudoed = workout.isKudoedBy(currentUser);
    int count = workout.getKudosCount();
    return Map.of(
        "newCount", count,
        "isKudoed", isKudoed);
  }

  @PostMapping("/{id}/comments")
  public String postComment(
      @PathVariable("id") Long workoutId,
      @RequestParam String content,
      @AuthenticationPrincipal User currentUser,
      Model model) {
    commentService.addComment(workoutId, currentUser.getEmail(), content);
    model.addAttribute("workout", workoutService.findById(workoutId).orElseThrow());
    return "components/comment-section :: comment-section";
  }

  @PostMapping("/{id}/comments/{commentId}/delete")
  public String deleteComment(
      @PathVariable("id") Long workoutId,
      @PathVariable Long commentId,
      @AuthenticationPrincipal User currentUser,
      Model model) {
    commentService.deleteComment(workoutId, commentId, currentUser);
    model.addAttribute("workout", workoutService.findById(workoutId).orElseThrow());
    return "components/comment-section :: comment-section";
  }

  @GetMapping("/new")
  public String newWorkoutForm(Model model, @AuthenticationPrincipal User currentUser) {
    populateWorkoutForm(model, new Workout());
    return "user-workout-form";
  }

  @GetMapping("/{id}/edit")
  public String editWorkoutForm(
      @PathVariable("id") Long workoutId, Model model, @AuthenticationPrincipal User currentUser) {
    Workout workout = workoutService.findById(workoutId).orElseThrow();
    populateWorkoutForm(model, workout);
    if (!isWorkoutOwner(workout, currentUser)) {
      return "redirect:/dashboard";
    }
    model.addAttribute("workout", workout);
    model.addAttribute("sports", sportService.findAll());
    return "user-workout-form";
  }

  @PostMapping("/save")
  public String saveWorkout(
      @ModelAttribute WorkoutDto workoutDto,
      @RequestParam(name = "exerciseIds", required = false) List<String> exerciseIds,
      @RequestParam(name = "sets", required = false) List<String> sets,
      @RequestParam(name = "reps", required = false) List<String> reps,
      @RequestParam(name = "weightKg", required = false) List<String> weightKg,
      @RequestParam(name = "durationMin", required = false) List<String> durationMin,
      @RequestParam(name = "distanceM", required = false) List<String> distanceM,
      @RequestParam(name = "averageBpm", required = false) List<String> averageBpm,
      @RequestParam(name = "elevationGainM", required = false) List<String> elevationGainM,
      @RequestParam(name = "maxSpeedKmh", required = false) List<String> maxSpeedKmh,
      @RequestParam(name = "score", required = false) List<String> score,
      @RequestParam(name = "attempts", required = false) List<String> attempts,
      @RequestParam(name = "successfulAttempts", required = false) List<String> successfulAttempts,
      @RequestParam(name = "accuracyPercent", required = false) List<String> accuracyPercent,
      @RequestParam(name = "heightM", required = false) List<String> heightM,
      @RequestParam(name = "depthM", required = false) List<String> depthM,
      @RequestParam(name = "laps", required = false) List<String> laps,
      @RequestParam(name = "rounds", required = false) List<String> rounds,
      @AuthenticationPrincipal User currentUser) {
    Workout workout;
    if (workoutDto.getId() != null) {
      Workout existingWorkout = workoutService.findById(workoutDto.getId()).orElseThrow();
      if (!isWorkoutOwner(existingWorkout, currentUser)) {
        return "redirect:/dashboard";
      }
      workout = existingWorkout;
    } else {
      workout = new Workout();
    }

    workout.setName(resolveWorkoutName(workoutDto));
    workout.setDescription(normalizeNullable(workoutDto.getDescription()));
    workout.setSport(workoutDto.getSport());
    workout.setDate(workoutDto.getDate() == null ? LocalDateTime.now() : workoutDto.getDate());
    workout.setRating(normalizeRating(workoutDto.getRating()));
    workout.setWeather(workoutDto.getWeather());
    workout.setAddress(workoutDto.getAddress());
    workout.setRating(workoutDto.getRating());
    workout.setDurationSec(null);
    if (workoutDto.getDuration() != null) {
      workout.setDurationMin(workoutDto.getDuration());
    }
    workout.setWorkoutExercises(
        buildWorkoutExercises(
            workout,
            exerciseIds,
            sets,
            reps,
            weightKg,
            durationMin,
            distanceM,
            averageBpm,
            elevationGainM,
            maxSpeedKmh,
            score,
            attempts,
            successfulAttempts,
            accuracyPercent,
            heightM,
            depthM,
            laps,
            rounds));
    workoutService.saveWorkout(workout, currentUser);
    return "redirect:/dashboard";
  }

  private String resolveWorkoutName(WorkoutDto workoutDto) {
    if (workoutDto.getName() != null && !workoutDto.getName().isBlank()) {
      return workoutDto.getName().trim();
    }
    throw new IllegalArgumentException("Workout name is mandatory");
  }

  @PostMapping("/{id}/delete")
  public String deleteWorkout(
      @PathVariable("id") Long workoutId, @AuthenticationPrincipal User currentUser) {
    Workout workout = workoutService.findById(workoutId).orElseThrow();
    if (!isWorkoutOwner(workout, currentUser) && !isAdmin(currentUser)) {
      return "redirect:/dashboard";
    }
    String sportName =
        workout.getSport() == null || workout.getSport().getName() == null
            ? "activité"
            : workout.getSport().getDisplayName();
    return "Séance " + sportName;
  }

  private List<WorkoutExercise> buildWorkoutExercises(
      Workout workout,
      List<String> exerciseIds,
      List<String> sets,
      List<String> reps,
      List<String> weightKg,
      List<String> durationMin,
      List<String> distanceM,
      List<String> averageBpm,
      List<String> elevationGainM,
      List<String> maxSpeedKmh,
      List<String> score,
      List<String> attempts,
      List<String> successfulAttempts,
      List<String> accuracyPercent,
      List<String> heightM,
      List<String> depthM,
      List<String> laps,
      List<String> rounds) {
    if (exerciseIds == null) {
      return List.of();
    }

    return java.util.stream.IntStream.range(0, exerciseIds.size())
        .mapToObj(
            index ->
                buildWorkoutExercise(
                    workout,
                    valueAt(exerciseIds, index),
                    valueAt(sets, index),
                    valueAt(reps, index),
                    valueAt(weightKg, index),
                    valueAt(durationMin, index),
                    valueAt(distanceM, index),
                    valueAt(averageBpm, index),
                    valueAt(elevationGainM, index),
                    valueAt(maxSpeedKmh, index),
                    valueAt(score, index),
                    valueAt(attempts, index),
                    valueAt(successfulAttempts, index),
                    valueAt(accuracyPercent, index),
                    valueAt(heightM, index),
                    valueAt(depthM, index),
                    valueAt(laps, index),
                    valueAt(rounds, index)))
        .flatMap(java.util.Optional::stream)
        .toList();
  }

  private java.util.Optional<WorkoutExercise> buildWorkoutExercise(
      Workout workout,
      String exerciseId,
      String sets,
      String reps,
      String weightKg,
      String durationMin,
      String distanceM,
      String averageBpm,
      String elevationGainM,
      String maxSpeedKmh,
      String score,
      String attempts,
      String successfulAttempts,
      String accuracyPercent,
      String heightM,
      String depthM,
      String laps,
      String rounds) {
    Long parsedExerciseId = parseLong(exerciseId);
    if (parsedExerciseId == null) {
      return java.util.Optional.empty();
    }

    return exerciseService
        .findById(parsedExerciseId)
        .map(
            exercise -> {
              WorkoutExercise workoutExercise = new WorkoutExercise();
              workoutExercise.setWorkout(workout);
              workoutExercise.setExercise(exercise);
              workoutExercise.setSets(parseInteger(sets));
              workoutExercise.setReps(parseInteger(reps));
              Double parsedWeightKg = parseDouble(weightKg);
              if (parsedWeightKg != null) {
                workoutExercise.setWeightG(parsedWeightKg);
              }
              Double parsedDurationMin = parseDouble(durationMin);
              if (parsedDurationMin != null) {
                workoutExercise.setDurationMin(parsedDurationMin);
              }
              workoutExercise.setDistanceM(parseDouble(distanceM));
              workoutExercise.setAverageBpm(parseDouble(averageBpm));
              workoutExercise.setElevationGainM(parseDouble(elevationGainM));
              workoutExercise.setMaxSpeedKmh(parseDouble(maxSpeedKmh));
              workoutExercise.setScore(parseDouble(score));
              workoutExercise.setAttempts(parseInteger(attempts));
              workoutExercise.setSuccessfulAttempts(parseInteger(successfulAttempts));
              workoutExercise.setAccuracyPercent(parseDouble(accuracyPercent));
              workoutExercise.setHeightM(parseDouble(heightM));
              workoutExercise.setDepthM(parseDouble(depthM));
              workoutExercise.setLaps(parseInteger(laps));
              workoutExercise.setRounds(parseInteger(rounds));
              return workoutExercise;
            });
  }

  private String valueAt(List<String> values, int index) {
    return values == null || index >= values.size() ? null : values.get(index);
  }

  private Long parseLong(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Long.valueOf(value);
  }

  private Integer parseInteger(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Integer.valueOf(value);
  }

  private Double parseDouble(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return Double.valueOf(value);
  }

  private void populateWorkoutForm(Model model, Workout workout) {
    List<Sport> sports = sportService.findAll();
    List<Exercise> exercises = exerciseService.getAll();
    model.addAttribute("workout", workout);
    model.addAttribute("sports", sports);
    model.addAttribute("exercises", exercises);
    model.addAttribute("exerciseSportNames", buildExerciseSportNames(exercises));
    model.addAttribute("sportFieldProfiles", sportService.buildFieldProfiles(sports));
  }

  private Map<Long, String> buildExerciseSportNames(List<Exercise> exercises) {
    Map<Long, String> exerciseSportNames = new HashMap<>();
    for (Exercise exercise : exercises) {
      String sportNames =
          exercise.getSports().stream()
              .filter(sport -> sport != null && sport.getName() != null)
              .map(Sport::getName)
              .distinct()
              .collect(Collectors.joining(","));
      exerciseSportNames.put(exercise.getId(), sportNames);
    }
    return exerciseSportNames;
  }

  private List<Badge> getUnlockedBadgesForWorkout(Workout workout) {
    if (workout == null
        || workout.getUser() == null
        || workout.getUser().getBadges() == null
        || workout.getSport() == null
        || workout.getSport().getName() == null) {
      return List.of();
    }

    String sportPrefix = workout.getSport().getDisplayName() + " - ";
    return workout.getUser().getBadges().stream()
        .filter(badge -> badge.getName() != null && badge.getName().startsWith(sportPrefix))
        .toList();
  }

  private boolean isWorkoutOwner(Workout workout, User currentUser) {
    return workout != null
        && workout.getUser() != null
        && workout.getUser().getId() != null
        && currentUser != null
        && currentUser.getId() != null
        && Objects.equals(workout.getUser().getId(), currentUser.getId());
  }

  private boolean isAdmin(User currentUser) {
    return currentUser != null && currentUser.getRole() == Role.ADMIN;
  }

  private Double normalizeRating(Double rating) {
    if (rating == null) {
      return null;
    }
    double rounded = Math.round(rating * 2.0) / 2.0;
    if (rounded < 0.5 || rounded > 5.0) {
      return null;
    }
    return rounded;
  }

  private String normalizeNullable(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
