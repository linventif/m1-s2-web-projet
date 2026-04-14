package web.sportflow.workout.comment;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutRepository;

@Service
public class CommentService {

  private final CommentRepository commentRepo;
  private final WorkoutRepository workoutRepo;
  private final UserRepository userRepo;

  @Autowired
  public CommentService(
      CommentRepository commentRepo, WorkoutRepository workoutRepo, UserRepository userRepo) {
    this.commentRepo = commentRepo;
    this.workoutRepo = workoutRepo;
    this.userRepo = userRepo;
  }

  @Transactional
  public void addComment(Long workoutId, String email, String content) {
    Workout workout =
        workoutRepo
            .findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout non trouvé"));

    User user =
        userRepo
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    Comment comment = new Comment(content, workout, user);

    commentRepo.save(comment);
  }

  public List<Comment> getCommentsForWorkout(Long workoutId) {
    return commentRepo.findByWorkoutIdOrderByCreatedAtAsc(workoutId);
  }
}
