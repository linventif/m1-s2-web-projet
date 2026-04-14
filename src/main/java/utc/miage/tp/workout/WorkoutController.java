package utc.miage.tp.workout;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import utc.miage.tp.user.User;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @PostMapping("/{id}/kudo")
    @ResponseBody
    public Map<String, Object> toggleKudo(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        workoutService.toggleKudo(id, currentUser);
        Workout workout = workoutService.findById(id).orElseThrow();
        boolean isKudoed = workout.isKudoedBy(currentUser);
        int count = workout.getKudosCount();
        return Map.of(
            "newCount", count,
            "isKudoed", isKudoed
        );
    }
}