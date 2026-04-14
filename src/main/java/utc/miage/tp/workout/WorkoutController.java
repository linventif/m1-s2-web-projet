package utc.miage.tp.workout;

import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import utc.miage.tp.user.User;
import utc.miage.tp.workout.comment.CommentService;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

  private final WorkoutService workoutService;
  private final CommentService commentService;

  public WorkoutController(WorkoutService workoutService, CommentService commentService) {
    this.workoutService = workoutService;
    this.commentService = commentService;
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
}
