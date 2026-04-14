package utc.miage.tp.workout.comment;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserRepository;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutRepository;

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
