package web.sportflow.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.badge.Badge;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.sport.Sport;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.WorkoutRepository;

@Service
public class ChallengeService {

  private final ChallengeRepository challengeRepository;
  private final FriendshipService friendshipService;
  private final UserRepository userRepository;
  private final WorkoutRepository workoutRepository;

  public ChallengeService(
      ChallengeRepository challengeRepository,
      FriendshipService friendshipService,
      UserRepository userRepository,
      WorkoutRepository workoutRepository) {
    this.challengeRepository = challengeRepository;
    this.friendshipService = friendshipService;
    this.userRepository = userRepository;
    this.workoutRepository = workoutRepository;
  }

  @Transactional
  public Challenge createChallenge(Challenge challenge) {
    Challenge newChallenge =
        new Challenge(
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getType(),
            challenge.getTargetValue(),
            challenge.getStartDate(),
            challenge.getEndDate(),
            challenge.getCreator(),
            challenge.isOfficial());
    newChallenge.setSports(
        challenge.getSports() == null ? new ArrayList<>() : new ArrayList<>(challenge.getSports()));
    newChallenge.setBadges(
        challenge.getBadges() == null ? new ArrayList<>() : new ArrayList<>(challenge.getBadges()));
    newChallenge.setParticipants(
        challenge.getParticipants() == null
            ? new ArrayList<>()
            : new ArrayList<>(challenge.getParticipants()));

    if (!newChallenge.isOfficial()) {
      newChallenge.getBadges().clear();
    } else {
      newChallenge.getParticipants().clear();
    }
    Challenge savedChallenge = challengeRepository.save(newChallenge);
    return challengeRepository.save(savedChallenge);
  }

  @Transactional(readOnly = true)
  public List<Challenge> getAll() {
    return challengeRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Challenge> searchChallenges(String query) {
    String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    return challengeRepository.findAll().stream()
        .peek(
            challenge -> {
              Hibernate.initialize(challenge.getSports());
              Hibernate.initialize(challenge.getBadges());
              Hibernate.initialize(challenge.getParticipants());
            })
        .filter(challenge -> normalizedQuery.isBlank() || matchesQuery(challenge, normalizedQuery))
        .sorted(
            Comparator.comparing(
                    Challenge::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
        .toList();
  }

  @Transactional
  public void joinChallenge(Long challengeId, User currentUser) {
    Challenge challenge = requireChallenge(challengeId);
    if (challenge.isOfficial()) {
      throw new IllegalArgumentException(
          "Ce challenge est officiel : il est automatiquement actif pour tout le monde.");
    }

    User participant = requireUser(currentUser);
    ensureChallengeIsOpen(challenge, "La date de fin du challenge est depassee.");

    boolean alreadyParticipating =
        challenge.getParticipants().stream()
            .anyMatch(user -> user != null && participant.getId().equals(user.getId()));
    if (!alreadyParticipating) {
      challenge.getParticipants().add(participant);
      challengeRepository.save(challenge);
    }
  }

  @Transactional
  public void leaveChallenge(Long challengeId, User currentUser) {
    Challenge challenge = requireChallenge(challengeId);
    if (challenge.isOfficial()) {
      throw new IllegalArgumentException("Vous ne pouvez pas quitter un challenge officiel.");
    }

    User participant = requireUser(currentUser);
    ensureChallengeIsOpen(
        challenge, "La participation ne peut plus etre annulee apres la date de fin.");

    boolean removed =
        challenge
            .getParticipants()
            .removeIf(user -> user != null && participant.getId().equals(user.getId()));
    if (!removed) {
      throw new IllegalArgumentException("Vous ne participez pas a ce challenge.");
    }
    challengeRepository.save(challenge);
  }

  @Transactional(readOnly = true)
  public List<Challenge> getFriendsAndUserChallenge(User currentUser) {
    List<Challenge> allChallenges = challengeRepository.findAll();
    List<User> visibleUsers = friendshipService.getCurrentUserAndFriend(currentUser);
    Set<Long> visibleCreatorIds =
        visibleUsers.stream()
            .map(User::getId)
            .filter(id -> id != null)
            .collect(LinkedHashSet::new, Set::add, Set::addAll);

    return allChallenges.stream()
        .filter(
            challenge ->
                challenge != null
                    && (challenge.isOfficial()
                        || (challenge.getCreator() != null
                            && challenge.getCreator().getId() != null
                            && visibleCreatorIds.contains(challenge.getCreator().getId()))))
        .toList();
  }

  @Transactional(readOnly = true)
  public Map<Long, ChallengeProgress> buildProgressByChallenge(
      List<Challenge> challenges, User currentUser) {
    if (currentUser == null
        || currentUser.getId() == null
        || challenges == null
        || challenges.isEmpty()) {
      return Map.of();
    }

    User participant = requireUser(currentUser);
    Map<Long, ChallengeProgress> progressByChallengeId = new LinkedHashMap<>();
    for (Challenge challenge : challenges) {
      if (challenge == null || challenge.getId() == null) {
        continue;
      }
      progressByChallengeId.put(challenge.getId(), computeProgress(challenge, participant));
    }
    return progressByChallengeId;
  }

  @Transactional
  public Set<Long> syncChallengeBadgesForUser(List<Challenge> challenges, User currentUser) {
    if (currentUser == null
        || currentUser.getId() == null
        || challenges == null
        || challenges.isEmpty()) {
      return Set.of();
    }

    User participant = requireUser(currentUser);
    Set<Long> unlockedBadgeIds = new LinkedHashSet<>();
    boolean changed = false;

    for (Challenge challenge : challenges) {
      if (challenge == null
          || challenge.getId() == null
          || challenge.getBadges() == null
          || challenge.getBadges().isEmpty()
          || !challenge.isOfficial()
          || !isEligibleForChallenge(challenge, participant)) {
        continue;
      }

      ChallengeProgress progress = computeProgress(challenge, participant);
      if (!progress.completed()) {
        continue;
      }

      for (Badge badge : challenge.getBadges()) {
        if (badge == null || badge.getId() == null) {
          continue;
        }
        if (!hasBadge(participant, badge.getId())) {
          participant.getBadges().add(badge);
          unlockedBadgeIds.add(badge.getId());
          changed = true;
        }
      }
    }

    if (changed) {
      userRepository.save(participant);
    }
    return unlockedBadgeIds;
  }

  @Transactional(readOnly = true)
  public List<Challenge> getOfficialChallenges(List<Challenge> challenges) {
    if (challenges == null || challenges.isEmpty()) {
      return List.of();
    }
    return challenges.stream()
        .filter(Challenge::isOfficial)
        .sorted(
            Comparator.<Challenge, Boolean>comparing(this::isTimedOfficial)
                .reversed()
                .thenComparing(
                    Challenge::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
        .toList();
  }

  @Transactional(readOnly = true)
  public List<Challenge> getCommunityChallenges(List<Challenge> challenges) {
    if (challenges == null || challenges.isEmpty()) {
      return List.of();
    }
    return challenges.stream().filter(challenge -> !challenge.isOfficial()).toList();
  }

  private ChallengeProgress computeProgress(Challenge challenge, User participant) {
    if (challenge == null
        || challenge.getType() == null
        || !isEligibleForChallenge(challenge, participant)) {
      return new ChallengeProgress(0.0, 0.0, 0, false, "");
    }

    double targetValue = challenge.getTargetValue() == null ? 0.0 : challenge.getTargetValue();
    List<Workout> workoutsInWindow = loadWorkoutsForChallenge(challenge, participant);
    double currentValue = computeCurrentValue(challenge.getType(), workoutsInWindow);
    int percentage = computePercentage(currentValue, targetValue);
    boolean completed = targetValue <= 0 || currentValue >= targetValue;

    return new ChallengeProgress(
        roundOneDecimal(currentValue),
        roundOneDecimal(targetValue),
        percentage,
        completed,
        resolveUnitLabel(challenge.getType()));
  }

  private boolean isEligibleForChallenge(Challenge challenge, User participant) {
    if (challenge == null || participant == null || participant.getId() == null) {
      return false;
    }
    if (challenge.isOfficial()) {
      return true;
    }
    if (challenge.getParticipants() == null) {
      return false;
    }
    return challenge.getParticipants().stream()
        .anyMatch(user -> user != null && participant.getId().equals(user.getId()));
  }

  private List<Workout> loadWorkoutsForChallenge(Challenge challenge, User participant) {
    LocalDate startDate =
        challenge.getStartDate() == null ? LocalDate.of(1970, 1, 1) : challenge.getStartDate();
    LocalDate endDate = challenge.getEndDate() == null ? LocalDate.now() : challenge.getEndDate();
    if (endDate.isBefore(startDate)) {
      return List.of();
    }

    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();
    List<Workout> workouts = workoutRepository.findByUserAndDateBetween(participant, start, end);

    if (challenge.getSports() == null || challenge.getSports().isEmpty()) {
      return workouts;
    }
    Set<Long> sportIds =
        challenge.getSports().stream()
            .map(Sport::getId)
            .filter(id -> id != null)
            .collect(LinkedHashSet::new, Set::add, Set::addAll);
    if (sportIds.isEmpty()) {
      return workouts;
    }
    return workouts.stream()
        .filter(
            workout ->
                workout != null
                    && workout.getSport() != null
                    && workout.getSport().getId() != null
                    && sportIds.contains(workout.getSport().getId()))
        .toList();
  }

  private double computeCurrentValue(ChallengeType type, List<Workout> workouts) {
    if (workouts == null || workouts.isEmpty()) {
      return 0.0;
    }

    return switch (type) {
      case DISTANCE -> workouts.stream().mapToDouble(this::getWorkoutDistanceKm).sum();
      case DUREE -> workouts.stream().mapToDouble(this::getWorkoutDurationMinutes).sum();
      case CALORIE -> workouts.stream().mapToDouble(this::getWorkoutCalories).sum();
      case REPETITION -> workouts.stream().mapToDouble(this::getWorkoutRepetitions).sum();
      case ENDURENCE -> workouts.stream().mapToDouble(this::getWorkoutDurationMinutes).sum();
    };
  }

  private double getWorkoutDistanceKm(Workout workout) {
    if (workout == null || workout.getWorkoutExercises() == null) {
      return 0.0;
    }
    return workout.getWorkoutExercises().stream()
            .filter(exercise -> exercise != null && exercise.getDistanceM() != null)
            .mapToDouble(WorkoutExercise::getDistanceM)
            .sum()
        / 1000.0;
  }

  private double getWorkoutDurationMinutes(Workout workout) {
    if (workout == null) {
      return 0.0;
    }
    if (workout.getDurationSec() != null && workout.getDurationSec() > 0) {
      return workout.getDurationSec() / 60.0;
    }
    if (workout.getWorkoutExercises() == null) {
      return 0.0;
    }
    return workout.getWorkoutExercises().stream()
            .filter(exercise -> exercise != null && exercise.getDurationSec() != null)
            .mapToDouble(WorkoutExercise::getDurationSec)
            .sum()
        / 60.0;
  }

  private double getWorkoutCalories(Workout workout) {
    if (workout == null) {
      return 0.0;
    }
    Double calories = workout.getCalorieBurn();
    return calories == null ? 0.0 : calories;
  }

  private double getWorkoutRepetitions(Workout workout) {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0.0;
    }

    double repetitions =
        workout.getWorkoutExercises().stream()
            .filter(
                exercise ->
                    exercise != null && exercise.getReps() != null && exercise.getReps() > 0)
            .mapToDouble(
                exercise ->
                    exercise.getSets() != null && exercise.getSets() > 0
                        ? exercise.getReps() * exercise.getSets()
                        : exercise.getReps())
            .sum();

    if (repetitions > 0) {
      return repetitions;
    }
    return 1.0;
  }

  private boolean matchesQuery(Challenge challenge, String normalizedQuery) {
    return contains(challenge.getTitle(), normalizedQuery)
        || contains(challenge.getDescription(), normalizedQuery)
        || (challenge.getType() != null
            && (contains(challenge.getType().name(), normalizedQuery)
                || contains(challenge.getType().getLabelFr(), normalizedQuery)))
        || contains(challenge.getSportNames(), normalizedQuery)
        || (challenge.isOfficial() && contains("officiel", normalizedQuery))
        || (!challenge.isOfficial() && contains("communaute", normalizedQuery));
  }

  private boolean contains(String value, String normalizedQuery) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
  }

  private String resolveUnitLabel(ChallengeType type) {
    return switch (type) {
      case DISTANCE -> "km";
      case DUREE -> "min";
      case CALORIE -> "kcal";
      case REPETITION -> "reps";
      case ENDURENCE -> "min";
    };
  }

  private int computePercentage(double currentValue, double targetValue) {
    if (targetValue <= 0) {
      return 100;
    }
    int raw = (int) Math.round((currentValue / targetValue) * 100.0);
    return Math.max(0, Math.min(raw, 100));
  }

  private double roundOneDecimal(double value) {
    return Math.round(value * 10.0) / 10.0;
  }

  private boolean hasBadge(User user, Long badgeId) {
    if (user == null || user.getBadges() == null || badgeId == null) {
      return false;
    }
    return user.getBadges().stream()
        .anyMatch(currentBadge -> currentBadge != null && badgeId.equals(currentBadge.getId()));
  }

  private boolean isTimedOfficial(Challenge challenge) {
    if (challenge == null) {
      return false;
    }
    return challenge.getStartDate() != null || challenge.getEndDate() != null;
  }

  private Challenge requireChallenge(Long challengeId) {
    return challengeRepository
        .findById(challengeId)
        .orElseThrow(() -> new IllegalArgumentException("Challenge introuvable."));
  }

  private User requireUser(User currentUser) {
    if (currentUser == null || currentUser.getId() == null) {
      throw new IllegalArgumentException("Utilisateur connecte introuvable.");
    }
    return userRepository
        .findById(currentUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("Utilisateur connecte introuvable."));
  }

  private void ensureChallengeIsOpen(Challenge challenge, String message) {
    if (challenge.getEndDate() != null && LocalDate.now().isAfter(challenge.getEndDate())) {
      throw new IllegalArgumentException(message);
    }
  }
}
