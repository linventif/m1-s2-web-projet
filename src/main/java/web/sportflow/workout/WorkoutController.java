package web.sportflow.workout;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import web.sportflow.sport.SportService;
import web.sportflow.user.User;
import web.sportflow.workout.comment.CommentService;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

  private final WorkoutService workoutService;
  private final CommentService commentService;
  private final SportService sportService;

  public WorkoutController(
      WorkoutService workoutService, CommentService commentService, SportService sportService) {
    this.workoutService = workoutService;
    this.commentService = commentService;
    this.sportService = sportService;
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

  @GetMapping("/new")
  public String newWorkoutForm(Model model, @AuthenticationPrincipal User currentUser) {
    model.addAttribute("workout", new Workout());
    model.addAttribute("sports", sportService.findAll());
    return "user-workout-form";
  }

  @GetMapping("/{id}/edit")
  public String editWorkoutForm(
      @PathVariable("id") Long workoutId, Model model, @AuthenticationPrincipal User currentUser) {
    Workout workout = workoutService.findById(workoutId).orElseThrow();
    model.addAttribute("workout", workout);
    model.addAttribute("sports", sportService.findAll());
    return "user-workout-form";
  }

  @PostMapping("/save")
  public String saveWorkout(Workout workout, @AuthenticationPrincipal User currentUser) {
    workoutService.saveWorkout(workout, currentUser);
    return "redirect:/dashboard";
  }
}
