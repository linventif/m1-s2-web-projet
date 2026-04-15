package web.sportflow.goal;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final FriendshipService friendshipService;
  private final UserRepository userRepository;

  public GoalService(
      GoalRepository goalRepository,
      FriendshipService friendshipService,
      UserRepository userRepository) {
    this.goalRepository = goalRepository;
    this.friendshipService = friendshipService;
    this.userRepository = userRepository;
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

  @Transactional(readOnly = true)
  public List<Goal> getFriendsAndUserGoal(User currentUser) {
    List<Long> visibleUserIds =
        friendshipService.getCurrentUserAndFriend(currentUser).stream().map(User::getId).toList();
    List<User> managedVisibleUsers = userRepository.findByIdIn(visibleUserIds);

    return Stream.concat(
            goalRepository.findByUserIn(managedVisibleUsers).stream(),
            managedVisibleUsers.stream().flatMap(user -> user.getGoals().stream()))
        .distinct()
        .toList();
  }
}
