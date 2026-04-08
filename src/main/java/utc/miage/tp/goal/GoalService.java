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

  @Transactional(readOnly = true)
  public List<Goal> getAllStatuts() {
    return goalRepository.findAll();
  }
}
