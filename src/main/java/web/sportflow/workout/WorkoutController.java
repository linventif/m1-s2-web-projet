package web.sportflow.workout;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import web.sportflow.openapi.BadRequestApiDoc;
import web.sportflow.openapi.ForbiddenApiDoc;
import web.sportflow.openapi.HtmlFragmentApiDoc;
import web.sportflow.openapi.HtmlRedirectApiDoc;
import web.sportflow.openapi.HtmlViewApiDoc;
import web.sportflow.openapi.InternalServerErrorApiDoc;
import web.sportflow.openapi.JsonSuccessApiDoc;
import web.sportflow.openapi.NotFoundApiDoc;
import web.sportflow.openapi.UnauthorizedApiDoc;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportService;
import web.sportflow.user.Role;
import web.sportflow.user.User;
import web.sportflow.workout.comment.CommentService;

@Tag(name = "Activités")
@Controller
@RequestMapping("/workouts")
@InternalServerErrorApiDoc
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Liste les activites",
      description =
          "Retourne la vue HTML listant les activites sportives visibles, avec les badges debloques par activite et les objets d'affichage utilises par l'interface utilisateur.")
  @HtmlViewApiDoc
  // @SuppressWarnings("CPD-END")
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Ajoute ou retire un kudo sur une activite",
      description =
          "Bascule l'etat du kudo de l'utilisateur connecte pour une activite donnee et retourne un payload JSON contenant le nouveau compteur et l'etat courant du kudo.")
  @JsonSuccessApiDoc
  @UnauthorizedApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Ajoute un commentaire a une activite",
      description =
          "Enregistre un commentaire pour l'activite cible puis retourne le fragment Thymeleaf de la section commentaires afin de mettre a jour l'interface.")
  @HtmlFragmentApiDoc
  @UnauthorizedApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Supprime un commentaire d'une activite",
      description =
          "Supprime un commentaire existant sur l'activite cible, sous reserve des droits applicables, puis retourne le fragment HTML actualise des commentaires.")
  @HtmlFragmentApiDoc
  @UnauthorizedApiDoc
  @ForbiddenApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Affiche le formulaire de creation d'une activite",
      description =
          "Retourne la vue HTML du formulaire de creation d'activite avec les sports, exercices et profils de champs necessaires au rendu dynamique du formulaire.")
  @HtmlViewApiDoc
  @UnauthorizedApiDoc
  // @SuppressWarnings("CPD-END")
  @GetMapping("/new")
  public String newWorkoutForm(Model model, @AuthenticationPrincipal User currentUser) {
    populateWorkoutForm(model, new Workout());
    return "user-workout-form";
  }

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Affiche le formulaire de modification d'une activite",
      description =
          "Charge une activite existante dans le formulaire d'edition. Si l'utilisateur connecte n'est pas proprietaire de l'activite, une redirection vers le tableau de bord est retournee.")
  @HtmlViewApiDoc
  @HtmlRedirectApiDoc
  @UnauthorizedApiDoc
  @ForbiddenApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Cree ou met a jour une activite",
      description =
          "Traite le formulaire de creation ou d'edition d'activite. L'operation reconstruit les exercices associes, applique les informations du workout puis redirige vers le tableau de bord.")
  @HtmlRedirectApiDoc
  @BadRequestApiDoc
  @UnauthorizedApiDoc
  @ForbiddenApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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
            workout, exerciseIds, sets, reps, weightKg, durationMin, distanceM, averageBpm));
    workoutService.saveWorkout(workout, currentUser);
    return "redirect:/dashboard";
  }

  private String resolveWorkoutName(WorkoutDto workoutDto) {
    if (workoutDto.getName() != null && !workoutDto.getName().isBlank()) {
      return workoutDto.getName().trim();
    }
    throw new IllegalArgumentException("Workout name is mandatory");
  }

  // @SuppressWarnings("CPD-START")
  @Operation(
      summary = "Supprime une activite",
      description =
          "Supprime une activite existante si l'utilisateur connecte en est proprietaire ou administrateur. Dans le cas contraire, une redirection vers le tableau de bord est retournee.")
  @ApiResponse(
      responseCode = "200",
      description = "Confirmation textuelle retournee par l'implementation courante",
      content =
          @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Seance Course")))
  @HtmlRedirectApiDoc
  @UnauthorizedApiDoc
  @ForbiddenApiDoc
  @NotFoundApiDoc
  // @SuppressWarnings("CPD-END")
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
      List<String> averageBpm) {
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
                    valueAt(averageBpm, index)))
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
      String averageBpm) {
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
    model.addAttribute("sportFieldProfiles", buildSportFieldProfiles(sports));
  }

  private Map<Long, String> buildExerciseSportNames(List<Exercise> exercises) {
    Map<Long, String> exerciseSportNames = new HashMap<>();
    for (Exercise exercise : exercises) {
      String sportNames =
          exercise.getSports().stream()
              .filter(sport -> sport != null && sport.getName() != null)
              .map(sport -> sport.getName().name())
              .distinct()
              .collect(Collectors.joining(","));
      exerciseSportNames.put(exercise.getId(), sportNames);
    }
    return exerciseSportNames;
  }

  private Map<Long, Map<String, Boolean>> buildSportFieldProfiles(List<Sport> sports) {
    Map<Long, Map<String, Boolean>> profiles = new HashMap<>();
    for (Sport sport : sports) {
      profiles.put(
          sport.getId(),
          Map.of(
              "distance", sport.isDistanceRelevant(),
              "strength", sport.isStrengthRelevant(),
              "mobility", sport.isMobilityRelevant()));
    }
    return profiles;
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
