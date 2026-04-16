package web.sportflow.config;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.challenge.ChallengeRepository;
import web.sportflow.exercise.ExerciseRepository;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.goal.GoalRepository;
import web.sportflow.sport.SportRepository;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.WorkoutRepository;

@Component
@ConditionalOnProperty(
    name = "app.data-initializer.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class ReferenceDataInitializer implements CommandLineRunner {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final BadgeRepository badgeRepository;
  private final ChallengeRepository challengeRepository;
  private final GoalRepository goalRepository;
  private final PasswordEncoder passwordEncoder;
  private final FriendshipService friendshipService;
  private final ExerciseRepository exerciseRepository;

  @Value("${app.avatar-upload-dir:upload_data/images/avatar}")
  private String avatarUploadDir;

  @Value("${app.badge-upload-dir:upload_data/images/badge}")
  private String badgeUploadDir;

  public ReferenceDataInitializer(
      UserRepository userRepository,
      SportRepository sportRepository,
      BadgeRepository badgeRepository,
      ChallengeRepository challengeRepository,
      GoalRepository goalRepository,
      PasswordEncoder passwordEncoder,
      WorkoutRepository workoutRepository,
      FriendshipService friendshipService,
      ExerciseRepository exerciseRepository) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.badgeRepository = badgeRepository;
    this.challengeRepository = challengeRepository;
    this.goalRepository = goalRepository;
    this.passwordEncoder = passwordEncoder;
    this.workoutRepository = workoutRepository;
    this.friendshipService = friendshipService;
    this.exerciseRepository = exerciseRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedDemoData();
  }

  private void seedDemoData() {
    // Users
    ReferenceUserCatalog.UsersDto usersDto =
        ReferenceUserCatalog.seed(userRepository, passwordEncoder);

    ReferenceDemoAssets.assignAvatars(usersDto.avatarByUser(), avatarUploadDir, userRepository);

    // Friendships
    ReferenceFriendshipCatalog.seed(friendshipService, usersDto.usersByKey());

    // Sports and exercises
    ReferenceSportExerciseCatalog.SportExercisesDto sportExercisesDto =
        ReferenceSportExerciseCatalog.seed(sportRepository, exerciseRepository);

    // Badges, challenges and goals
    ReferenceBadgeCatalog.BadgesDto badgesDto =
        ReferenceBadgeCatalog.seed(badgeRepository, sportExercisesDto.sportsByName());
    ReferenceDemoAssets.assignBadgeIcons(badgesDto.allBadges(), badgeUploadDir, badgeRepository);
    ReferenceChallengeCatalog.seed(
        challengeRepository,
        LocalDate.now(),
        usersDto.user("admin"),
        sportExercisesDto.sportsByName(),
        usersDto.usersByKey(),
        badgesDto.badgesByKey());
    ReferenceGoalCatalog.seed(goalRepository, usersDto.usersByKey());
    ReferenceBadgeCatalog.assignToUsers(usersDto.usersByKey(), badgesDto.badgesByKey());
    userRepository.saveAll(usersDto.usersByKey().values());

    ReferenceWorkoutCatalog.seed(workoutRepository, usersDto, sportExercisesDto);
  }
}
