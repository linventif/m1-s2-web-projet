package web.sportflow.workout.comment;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import web.sportflow.user.Role;
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

  @Transactional
  public void deleteComment(Long workoutId, Long commentId, User currentUser) {
    if (currentUser == null || currentUser.getId() == null) {
      throw new IllegalArgumentException("Utilisateur non authentifie.");
    }

    Comment comment =
        commentRepo
            .findById(commentId)
            .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
    if (comment.getWorkout() == null
        || comment.getWorkout().getId() == null
        || !comment.getWorkout().getId().equals(workoutId)) {
      throw new IllegalArgumentException("Commentaire invalide pour cette activité.");
    }

    boolean isAuthor =
        comment.getAuthor() != null
            && comment.getAuthor().getId() != null
            && comment.getAuthor().getId().equals(currentUser.getId());
    boolean isAdmin = currentUser.getRole() == Role.ADMIN;
    if (!isAuthor && !isAdmin) {
      throw new IllegalArgumentException("Suppression non autorisée.");
    }

    commentRepo.delete(comment);
  }
}
