package utc.miage.tp.goal;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoalService {

  private final GoalRepository goalRepository;

  public GoalService(GoalRepository goalRepository) {
    this.goalRepository = goalRepository;
  }

  @Transactional
  public Goal createGoal(Goal goal) {
    Goal newGoal =
        new Goal(
            goal.getLabel(),
            goal.getType(),
            goal.getTargetValue(),
            goal.getCurrentValue(),
            goal.getUnit(),
            goal.getUser());

    Goal savedGoal = goalRepository.save(newGoal);

    return goalRepository.save(savedGoal);
  }

  @Transactional(readOnly = true)
  public List<Goal> getAll() {
    return goalRepository.findAll();
  }
}
