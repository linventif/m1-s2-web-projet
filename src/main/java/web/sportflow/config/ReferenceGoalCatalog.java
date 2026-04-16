package web.sportflow.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalRepository;
import web.sportflow.goal.GoalType;
import web.sportflow.user.User;

final class ReferenceGoalCatalog {

  private static final List<GoalDto> GOALS =
      List.of(
          new GoalDto(
              "alice", "Objectif running mensuel Alice", GoalType.DISTANCE, 50.0, 22.5, "km"),
          new GoalDto("nick", "Objectif natation Nick", GoalType.DUREE, 240.0, 75.0, "min"),
          new GoalDto(
              "astrid", "Objectif escalade Astrid", GoalType.CALORIES, 1800.0, 650.0, "kcal"),
          new GoalDto("stoick", "Objectif cyclisme Stoick", GoalType.DISTANCE, 120.0, 48.0, "km"),
          new GoalDto(
              "bogo", "Objectif musculation Bogo", GoalType.REPETITIONS, 300.0, 120.0, "reps"),
          new GoalDto("benoit", "Objectif reprise Benoit", GoalType.DISTANCE, 35.0, 12.0, "km"),
          new GoalDto("owen", "Objectif pressing Owen", GoalType.DISTANCE, 24.0, 9.0, "km"),
          new GoalDto("admin", "Objectif demo admin", GoalType.DUREE, 180.0, 45.0, "min"),
          new GoalDto("judy", "Objectif agilite Judy", GoalType.REPETITIONS, 80.0, 36.0, "reps"),
          new GoalDto("bellwether", "Objectif mobilite Dawn", GoalType.DUREE, 150.0, 40.0, "min"),
          new GoalDto("hiccup", "Objectif endurance Hiccup", GoalType.DISTANCE, 45.0, 16.0, "km"),
          new GoalDto(
              "fishlegs", "Objectif bloc Fishlegs", GoalType.CALORIES, 1200.0, 410.0, "kcal"),
          new GoalDto("rodney", "Objectif parkour Rodney", GoalType.REPETITIONS, 10.0, 4.0, "runs"),
          new GoalDto("cappy", "Objectif yoga Cappy", GoalType.DUREE, 210.0, 75.0, "min"),
          new GoalDto("fender", "Objectif plongee Fender", GoalType.DUREE, 120.0, 35.0, "min"),
          new GoalDto("bigweld", "Objectif rando Bigweld", GoalType.DISTANCE, 40.0, 12.0, "km"),
          new GoalDto(
              "shifu", "Objectif precision Shifu", GoalType.REPETITIONS, 220.0, 90.0, "reps"),
          new GoalDto("oogway", "Objectif grand air Oogway", GoalType.DISTANCE, 25.0, 8.0, "km"),
          new GoalDto("po", "Objectif cardio Po", GoalType.CALORIES, 1600.0, 520.0, "kcal"),
          new GoalDto(
              "taiLung",
              "Objectif puissance Tai Lung",
              GoalType.REPETITIONS,
              320.0,
              140.0,
              "reps"));

  private ReferenceGoalCatalog() {}

  static List<Goal> seed(GoalRepository goalRepository, Map<String, User> usersByKey) {
    List<Goal> goals = new ArrayList<>();
    for (GoalDto dto : GOALS) {
      goals.add(
          new Goal(
              dto.label(),
              dto.type(),
              dto.targetValue(),
              dto.currentValue(),
              dto.unit(),
              requireUser(usersByKey, dto.userKey())));
    }

    List<Goal> savedGoals = goalRepository.saveAll(goals);
    for (Goal goal : savedGoals) {
      User user = goal.getUser();
      if (user != null && !user.getGoals().contains(goal)) {
        user.getGoals().add(goal);
      }
    }
    return savedGoals;
  }

  private static User requireUser(Map<String, User> usersByKey, String userKey) {
    User user = usersByKey.get(userKey);
    if (user == null) {
      throw new IllegalStateException("Utilisateur de demo introuvable: " + userKey);
    }
    return user;
  }

  private record GoalDto(
      String userKey,
      String label,
      GoalType type,
      Double targetValue,
      Double currentValue,
      String unit) {}
}
