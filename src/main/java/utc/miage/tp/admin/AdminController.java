package utc.miage.tp.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utc.miage.tp.badge.Badge;
import utc.miage.tp.badge.BadgeRepository;
import utc.miage.tp.challenge.Challenge;
import utc.miage.tp.challenge.ChallengeRepository;
import utc.miage.tp.challenge.ChallengeType;
import utc.miage.tp.friendship.Friendship;
import utc.miage.tp.friendship.FriendshipRepository;
import utc.miage.tp.friendship.FriendshipStatus;
import utc.miage.tp.goal.Goal;
import utc.miage.tp.goal.GoalRepository;
import utc.miage.tp.goal.GoalType;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.sport.SportRepository;
import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Role;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserRepository;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private static final String DEFAULT_BADGE_ICON = "/images/badge/running_5km.png";

  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final WorkoutRepository workoutRepository;
  private final BadgeRepository badgeRepository;
  private final GoalRepository goalRepository;
  private final ChallengeRepository challengeRepository;
  private final FriendshipRepository friendshipRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminController(
      UserRepository userRepository,
      SportRepository sportRepository,
      WorkoutRepository workoutRepository,
      BadgeRepository badgeRepository,
      GoalRepository goalRepository,
      ChallengeRepository challengeRepository,
      FriendshipRepository friendshipRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.workoutRepository = workoutRepository;
    this.badgeRepository = badgeRepository;
    this.goalRepository = goalRepository;
    this.challengeRepository = challengeRepository;
    this.friendshipRepository = friendshipRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping({"", "/", "/panel"})
  public String showPanel(Model model) {
    List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    users.removeIf(Objects::isNull);
    users.forEach(
        user -> {
          user.getSports().removeIf(Objects::isNull);
          user.getBadges().removeIf(Objects::isNull);
          user.getGoals().removeIf(Objects::isNull);
        });

    List<Sport> sports = sportRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    sports.removeIf(Objects::isNull);

    List<Workout> workouts = workoutRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    workouts.removeIf(Objects::isNull);

    List<Badge> badges = badgeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    badges.removeIf(Objects::isNull);

    List<Goal> goals = goalRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    goals.removeIf(Objects::isNull);

    List<Challenge> challenges = challengeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    challenges.removeIf(Objects::isNull);

    List<Friendship> friendships = friendshipRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    friendships.removeIf(Objects::isNull);

    model.addAttribute("users", users);
    model.addAttribute("sports", sports);
    model.addAttribute("workouts", workouts);
    model.addAttribute("badges", badges);
    model.addAttribute("goals", goals);
    model.addAttribute("challenges", challenges);
    model.addAttribute("friendships", friendships);

    model.addAttribute("roles", Role.values());
    model.addAttribute("sexes", Sex.values());
    model.addAttribute("levels", PracticeLevel.values());
    model.addAttribute("goalTypes", GoalType.values());
    model.addAttribute("challengeTypes", ChallengeType.values());
    model.addAttribute("friendshipStatuses", FriendshipStatus.values());

    model.addAttribute("today", LocalDate.now());
    model.addAttribute("currentDateTime", LocalDateTime.now().withSecond(0).withNano(0));
    return "admin-panel";
  }

  @PostMapping("/users/create")
  @Transactional
  public String createUser(
      @RequestParam String firstname,
      @RequestParam String lastname,
      @RequestParam String email,
      @RequestParam String password,
      @RequestParam Role role,
      @RequestParam Double weight,
      @RequestParam Double height,
      @RequestParam Sex sex,
      @RequestParam PracticeLevel level,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate birthDate,
      @RequestParam(required = false) String profileImagePath,
      @RequestParam(name = "sportIds", required = false) List<Long> sportIds,
      @RequestParam(name = "badgeIds", required = false) List<Long> badgeIds,
      RedirectAttributes redirectAttributes) {
    try {
      String normalizedEmail = normalizeEmail(email);
      if (userRepository.existsByEmail(normalizedEmail)) {
        throw new IllegalArgumentException("Un utilisateur avec cet email existe deja.");
      }

      User user = new User();
      user.setFirstname(requireText(firstname, "Le prenom est obligatoire."));
      user.setLastname(requireText(lastname, "Le nom est obligatoire."));
      user.setEmail(requireText(normalizedEmail, "L'email est obligatoire."));
      user.setPassword(
          passwordEncoder.encode(requireText(password, "Le mot de passe est obligatoire.")));
      user.setRole(role == null ? Role.USER : role);
      user.setWeight(requirePositive(weight, "Le poids doit etre superieur a 0."));
      user.setHeight(requirePositive(height, "La taille doit etre superieure a 0."));
      user.setSex(sex);
      user.setLevel(level);
      if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("La date de naissance ne peut pas etre dans le futur.");
      }
      user.setBirthDate(birthDate);
      user.setProfileImagePath(normalizeNullable(profileImagePath));

      user.getSports().addAll(resolveSports(sportIds));
      user.getBadges().addAll(resolveBadges(badgeIds));

      userRepository.save(user);
      redirectAttributes.addFlashAttribute("message", "Utilisateur cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("users");
  }

  @PostMapping("/users/{userId}/update")
  @Transactional
  public String updateUser(
      @PathVariable Long userId,
      @RequestParam String firstname,
      @RequestParam String lastname,
      @RequestParam String email,
      @RequestParam(required = false) String password,
      @RequestParam Role role,
      @RequestParam Double weight,
      @RequestParam Double height,
      @RequestParam Sex sex,
      @RequestParam PracticeLevel level,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate birthDate,
      @RequestParam(required = false) String profileImagePath,
      @RequestParam(name = "sportIds", required = false) List<Long> sportIds,
      @RequestParam(name = "badgeIds", required = false) List<Long> badgeIds,
      RedirectAttributes redirectAttributes) {
    try {
      User user = requireUser(userId);
      String normalizedEmail = normalizeEmail(email);
      userRepository
          .findByEmail(normalizedEmail)
          .filter(existing -> !existing.getId().equals(userId))
          .ifPresent(
              existing -> {
                throw new IllegalArgumentException("Un utilisateur avec cet email existe deja.");
              });

      user.setFirstname(requireText(firstname, "Le prenom est obligatoire."));
      user.setLastname(requireText(lastname, "Le nom est obligatoire."));
      user.setEmail(requireText(normalizedEmail, "L'email est obligatoire."));
      if (password != null && !password.isBlank()) {
        user.setPassword(passwordEncoder.encode(password.trim()));
      }
      user.setRole(role == null ? Role.USER : role);
      user.setWeight(requirePositive(weight, "Le poids doit etre superieur a 0."));
      user.setHeight(requirePositive(height, "La taille doit etre superieure a 0."));
      user.setSex(sex);
      user.setLevel(level);
      if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("La date de naissance ne peut pas etre dans le futur.");
      }
      user.setBirthDate(birthDate);
      user.setProfileImagePath(normalizeNullable(profileImagePath));

      user.getSports().clear();
      user.getSports().addAll(resolveSports(sportIds));
      user.getBadges().clear();
      user.getBadges().addAll(resolveBadges(badgeIds));

      userRepository.save(user);
      redirectAttributes.addFlashAttribute("message", "Utilisateur mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("users");
  }

  @PostMapping("/users/{userId}/delete")
  @Transactional
  public String deleteUser(
      @PathVariable Long userId,
      @AuthenticationPrincipal User currentUser,
      RedirectAttributes redirectAttributes) {
    try {
      if (currentUser != null && userId.equals(currentUser.getId())) {
        throw new IllegalArgumentException("Vous ne pouvez pas supprimer votre propre compte.");
      }

      User user = requireUser(userId);

      List<Goal> ownedGoals =
          goalRepository.findAll().stream()
              .filter(goal -> goal.getUser() != null && userId.equals(goal.getUser().getId()))
              .toList();
      for (Goal goal : ownedGoals) {
        removeGoalFromUsers(goal.getId());
      }
      goalRepository.deleteAll(ownedGoals);

      List<Workout> workoutsToDelete =
          workoutRepository.findAll().stream()
              .filter(
                  workout -> workout.getUser() != null && userId.equals(workout.getUser().getId()))
              .toList();
      workoutRepository.deleteAll(workoutsToDelete);

      List<Challenge> challengesToDelete =
          challengeRepository.findAll().stream()
              .filter(
                  challenge ->
                      challenge.getCreator() != null
                          && userId.equals(challenge.getCreator().getId()))
              .toList();
      challengeRepository.deleteAll(challengesToDelete);

      List<Friendship> friendshipsToDelete =
          friendshipRepository.findAll().stream()
              .filter(
                  friendship ->
                      (friendship.getRequester() != null
                              && userId.equals(friendship.getRequester().getId()))
                          || (friendship.getAddressee() != null
                              && userId.equals(friendship.getAddressee().getId())))
              .toList();
      friendshipRepository.deleteAll(friendshipsToDelete);

      userRepository.delete(user);
      redirectAttributes.addFlashAttribute("message", "Utilisateur supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("users");
  }

  @PostMapping("/sports/create")
  @Transactional
  public String createSport(
      @RequestParam String name,
      @RequestParam(name = "caloriesPerMinute") Double caloriesPerMinute,
      RedirectAttributes redirectAttributes) {
    try {
      String normalizedName = requireText(name, "Le nom du sport est obligatoire.");
      boolean alreadyExists =
          sportRepository.findAll().stream()
              .anyMatch(sport -> sport.getName().equalsIgnoreCase(normalizedName));
      if (alreadyExists) {
        throw new IllegalArgumentException("Un sport avec ce nom existe deja.");
      }

      Sport sport = new Sport();
      sport.setName(normalizedName);
      sport.setCaloryPerMinutes(requirePositive(caloriesPerMinute, "La valeur doit etre > 0."));

      sportRepository.save(sport);
      redirectAttributes.addFlashAttribute("message", "Sport cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("sports");
  }

  @PostMapping("/sports/{sportId}/update")
  @Transactional
  public String updateSport(
      @PathVariable Long sportId,
      @RequestParam String name,
      @RequestParam(name = "caloriesPerMinute") Double caloriesPerMinute,
      RedirectAttributes redirectAttributes) {
    try {
      Sport sport = requireSport(sportId);
      String normalizedName = requireText(name, "Le nom du sport est obligatoire.");
      boolean alreadyExists =
          sportRepository.findAll().stream()
              .anyMatch(
                  currentSport ->
                      !currentSport.getId().equals(sportId)
                          && currentSport.getName().equalsIgnoreCase(normalizedName));
      if (alreadyExists) {
        throw new IllegalArgumentException("Un sport avec ce nom existe deja.");
      }

      sport.setName(normalizedName);
      sport.setCaloryPerMinutes(requirePositive(caloriesPerMinute, "La valeur doit etre > 0."));
      sportRepository.save(sport);
      redirectAttributes.addFlashAttribute("message", "Sport mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("sports");
  }

  @PostMapping("/sports/{sportId}/delete")
  @Transactional
  public String deleteSport(@PathVariable Long sportId, RedirectAttributes redirectAttributes) {
    try {
      Sport sport = requireSport(sportId);

      for (User user : userRepository.findAll()) {
        boolean changed =
            user.getSports().removeIf(currentSport -> sportId.equals(currentSport.getId()));
        if (changed) {
          userRepository.save(user);
        }
      }

      List<Workout> workoutsToDelete =
          workoutRepository.findAll().stream()
              .filter(
                  workout ->
                      workout.getSport() != null && sportId.equals(workout.getSport().getId()))
              .toList();
      workoutRepository.deleteAll(workoutsToDelete);

      sportRepository.delete(sport);
      redirectAttributes.addFlashAttribute("message", "Sport supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("sports");
  }

  @PostMapping("/badges/create")
  @Transactional
  public String createBadge(
      @RequestParam String name,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String iconPath,
      RedirectAttributes redirectAttributes) {
    try {
      String normalizedName = requireText(name, "Le nom du badge est obligatoire.");
      if (badgeRepository.existsByName(normalizedName)) {
        throw new IllegalArgumentException("Un badge avec ce nom existe deja.");
      }

      Badge badge = new Badge();
      badge.setName(normalizedName);
      badge.setDescription(normalizeNullable(description));
      badge.setIconPath(normalizeIconPath(iconPath));
      badgeRepository.save(badge);

      redirectAttributes.addFlashAttribute("message", "Badge cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("badges");
  }

  @PostMapping("/badges/{badgeId}/update")
  @Transactional
  public String updateBadge(
      @PathVariable Long badgeId,
      @RequestParam String name,
      @RequestParam(required = false) String description,
      @RequestParam(required = false) String iconPath,
      RedirectAttributes redirectAttributes) {
    try {
      Badge badge = requireBadge(badgeId);
      String normalizedName = requireText(name, "Le nom du badge est obligatoire.");
      badgeRepository
          .findByName(normalizedName)
          .filter(existing -> !existing.getId().equals(badgeId))
          .ifPresent(
              existing -> {
                throw new IllegalArgumentException("Un badge avec ce nom existe deja.");
              });

      badge.setName(normalizedName);
      badge.setDescription(normalizeNullable(description));
      badge.setIconPath(normalizeIconPath(iconPath));
      badgeRepository.save(badge);

      redirectAttributes.addFlashAttribute("message", "Badge mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("badges");
  }

  @PostMapping("/badges/{badgeId}/delete")
  @Transactional
  public String deleteBadge(@PathVariable Long badgeId, RedirectAttributes redirectAttributes) {
    try {
      Badge badge = requireBadge(badgeId);

      for (User user : userRepository.findAll()) {
        boolean changed =
            user.getBadges().removeIf(currentBadge -> badgeId.equals(currentBadge.getId()));
        if (changed) {
          userRepository.save(user);
        }
      }

      badgeRepository.delete(badge);
      redirectAttributes.addFlashAttribute("message", "Badge supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("badges");
  }

  @PostMapping("/goals/create")
  @Transactional
  public String createGoal(
      @RequestParam String label,
      @RequestParam GoalType type,
      @RequestParam Double targetValue,
      @RequestParam Double currentValue,
      @RequestParam String unit,
      @RequestParam Long userId,
      RedirectAttributes redirectAttributes) {
    try {
      User owner = requireUser(userId);

      Goal goal = new Goal();
      goal.setLabel(requireText(label, "Le libelle de l'objectif est obligatoire."));
      goal.setType(type);
      goal.setTargetValue(requirePositive(targetValue, "La cible doit etre superieure a 0."));
      goal.setCurrentValue(
          requireNonNegative(currentValue, "La progression ne peut pas etre negative."));
      goal.setUnit(requireText(unit, "L'unite est obligatoire."));
      goal.setUser(owner);

      Goal savedGoal = goalRepository.save(goal);
      syncGoalMembership(savedGoal, owner);
      redirectAttributes.addFlashAttribute("message", "Objectif cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("goals");
  }

  @PostMapping("/goals/{goalId}/update")
  @Transactional
  public String updateGoal(
      @PathVariable Long goalId,
      @RequestParam String label,
      @RequestParam GoalType type,
      @RequestParam Double targetValue,
      @RequestParam Double currentValue,
      @RequestParam String unit,
      @RequestParam Long userId,
      RedirectAttributes redirectAttributes) {
    try {
      Goal goal = requireGoal(goalId);
      User owner = requireUser(userId);

      goal.setLabel(requireText(label, "Le libelle de l'objectif est obligatoire."));
      goal.setType(type);
      goal.setTargetValue(requirePositive(targetValue, "La cible doit etre superieure a 0."));
      goal.setCurrentValue(
          requireNonNegative(currentValue, "La progression ne peut pas etre negative."));
      goal.setUnit(requireText(unit, "L'unite est obligatoire."));
      goal.setUser(owner);

      Goal savedGoal = goalRepository.save(goal);
      syncGoalMembership(savedGoal, owner);
      redirectAttributes.addFlashAttribute("message", "Objectif mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("goals");
  }

  @PostMapping("/goals/{goalId}/delete")
  @Transactional
  public String deleteGoal(@PathVariable Long goalId, RedirectAttributes redirectAttributes) {
    try {
      Goal goal = requireGoal(goalId);
      removeGoalFromUsers(goalId);
      goalRepository.delete(goal);
      redirectAttributes.addFlashAttribute("message", "Objectif supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("goals");
  }

  @PostMapping("/workouts/create")
  @Transactional
  public String createWorkout(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
      @RequestParam Double distance,
      @RequestParam Double duration,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String rating,
      @RequestParam Long sportId,
      @RequestParam Long userId,
      RedirectAttributes redirectAttributes) {
    try {
      Workout workout = new Workout();
      workout.setDate(date);
      workout.setDistance(requirePositive(distance, "La distance doit etre superieure a 0."));
      workout.setDuration(requirePositive(duration, "La duree doit etre superieure a 0."));
      workout.setAddress(normalizeNullable(address));
      workout.setRating(parseOptionalRating(rating));
      workout.setSport(requireSport(sportId));
      workout.setUser(requireUser(userId));

      workoutRepository.save(workout);
      redirectAttributes.addFlashAttribute("message", "Workout cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("workouts");
  }

  @PostMapping("/workouts/{workoutId}/update")
  @Transactional
  public String updateWorkout(
      @PathVariable Long workoutId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
      @RequestParam Double distance,
      @RequestParam Double duration,
      @RequestParam(required = false) String address,
      @RequestParam(required = false) String rating,
      @RequestParam Long sportId,
      @RequestParam Long userId,
      RedirectAttributes redirectAttributes) {
    try {
      Workout workout = requireWorkout(workoutId);
      workout.setDate(date);
      workout.setDistance(requirePositive(distance, "La distance doit etre superieure a 0."));
      workout.setDuration(requirePositive(duration, "La duree doit etre superieure a 0."));
      workout.setAddress(normalizeNullable(address));
      workout.setRating(parseOptionalRating(rating));
      workout.setSport(requireSport(sportId));
      workout.setUser(requireUser(userId));

      workoutRepository.save(workout);
      redirectAttributes.addFlashAttribute("message", "Workout mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("workouts");
  }

  @PostMapping("/workouts/{workoutId}/delete")
  @Transactional
  public String deleteWorkout(@PathVariable Long workoutId, RedirectAttributes redirectAttributes) {
    try {
      Workout workout = requireWorkout(workoutId);
      workoutRepository.delete(workout);
      redirectAttributes.addFlashAttribute("message", "Workout supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("workouts");
  }

  @PostMapping("/challenges/create")
  @Transactional
  public String createChallenge(
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam ChallengeType type,
      @RequestParam Double targetValue,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam Long creatorId,
      RedirectAttributes redirectAttributes) {
    try {
      validateChallengeDates(startDate, endDate);

      Challenge challenge = new Challenge();
      challenge.setTitle(requireText(title, "Le titre du challenge est obligatoire."));
      challenge.setDescription(normalizeNullable(description));
      challenge.setType(type);
      challenge.setTargetValue(requirePositive(targetValue, "La cible doit etre superieure a 0."));
      challenge.setStartDate(startDate);
      challenge.setEndDate(endDate);
      challenge.setCreator(requireUser(creatorId));

      challengeRepository.save(challenge);
      redirectAttributes.addFlashAttribute("message", "Challenge cree avec succes.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("challenges");
  }

  @PostMapping("/challenges/{challengeId}/update")
  @Transactional
  public String updateChallenge(
      @PathVariable Long challengeId,
      @RequestParam String title,
      @RequestParam(required = false) String description,
      @RequestParam ChallengeType type,
      @RequestParam Double targetValue,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam Long creatorId,
      RedirectAttributes redirectAttributes) {
    try {
      validateChallengeDates(startDate, endDate);
      Challenge challenge = requireChallenge(challengeId);

      challenge.setTitle(requireText(title, "Le titre du challenge est obligatoire."));
      challenge.setDescription(normalizeNullable(description));
      challenge.setType(type);
      challenge.setTargetValue(requirePositive(targetValue, "La cible doit etre superieure a 0."));
      challenge.setStartDate(startDate);
      challenge.setEndDate(endDate);
      challenge.setCreator(requireUser(creatorId));

      challengeRepository.save(challenge);
      redirectAttributes.addFlashAttribute("message", "Challenge mis a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("challenges");
  }

  @PostMapping("/challenges/{challengeId}/delete")
  @Transactional
  public String deleteChallenge(
      @PathVariable Long challengeId, RedirectAttributes redirectAttributes) {
    try {
      Challenge challenge = requireChallenge(challengeId);
      challengeRepository.delete(challenge);
      redirectAttributes.addFlashAttribute("message", "Challenge supprime.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("challenges");
  }

  @PostMapping("/friendships/create")
  @Transactional
  public String createFriendship(
      @RequestParam Long requesterId,
      @RequestParam Long addresseeId,
      @RequestParam FriendshipStatus status,
      RedirectAttributes redirectAttributes) {
    try {
      validateFriendshipUsers(requesterId, addresseeId);
      User requester = requireUser(requesterId);
      User addressee = requireUser(addresseeId);

      Friendship friendship =
          friendshipRepository
              .findRelationshipBetween(requesterId, addresseeId)
              .orElseGet(() -> new Friendship(requester, addressee, status));
      friendship.setRequester(requester);
      friendship.setAddressee(addressee);
      friendship.setStatus(status);

      friendshipRepository.save(friendship);
      redirectAttributes.addFlashAttribute("message", "Relation d'amitie enregistree.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("friendships");
  }

  @PostMapping("/friendships/{friendshipId}/update")
  @Transactional
  public String updateFriendship(
      @PathVariable Long friendshipId,
      @RequestParam Long requesterId,
      @RequestParam Long addresseeId,
      @RequestParam FriendshipStatus status,
      RedirectAttributes redirectAttributes) {
    try {
      validateFriendshipUsers(requesterId, addresseeId);
      Friendship friendship = requireFriendship(friendshipId);
      friendship.setRequester(requireUser(requesterId));
      friendship.setAddressee(requireUser(addresseeId));
      friendship.setStatus(status);

      friendshipRepository.save(friendship);
      redirectAttributes.addFlashAttribute("message", "Relation d'amitie mise a jour.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("friendships");
  }

  @PostMapping("/friendships/{friendshipId}/delete")
  @Transactional
  public String deleteFriendship(
      @PathVariable Long friendshipId, RedirectAttributes redirectAttributes) {
    try {
      Friendship friendship = requireFriendship(friendshipId);
      friendshipRepository.delete(friendship);
      redirectAttributes.addFlashAttribute("message", "Relation d'amitie supprimee.");
    } catch (Exception exception) {
      redirectAttributes.addFlashAttribute("errorMessage", buildErrorMessage(exception));
    }
    return redirectTo("friendships");
  }

  private String redirectTo(String section) {
    return "redirect:/admin#" + section;
  }

  private String buildErrorMessage(Exception exception) {
    if (exception instanceof DataIntegrityViolationException) {
      return "Operation refusee par la base de donnees. Verifiez les dependances.";
    }
    String message = exception.getMessage();
    if (message == null || message.isBlank()) {
      return "Operation impossible.";
    }
    return message;
  }

  private String requireText(String value, String errorMessage) {
    String normalized = normalizeNullable(value);
    if (normalized == null) {
      throw new IllegalArgumentException(errorMessage);
    }
    return normalized;
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isBlank() ? null : normalized;
  }

  private String normalizeEmail(String email) {
    if (email == null) {
      return "";
    }
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private Double requirePositive(Double value, String errorMessage) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  private Double requireNonNegative(Double value, String errorMessage) {
    if (value == null || value < 0) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  private Integer parseOptionalRating(String rawValue) {
    String normalized = normalizeNullable(rawValue);
    if (normalized == null) {
      return null;
    }
    try {
      int rating = Integer.parseInt(normalized);
      if (rating < 1 || rating > 5) {
        throw new IllegalArgumentException("La note doit etre comprise entre 1 et 5.");
      }
      return rating;
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("La note doit etre un entier entre 1 et 5.");
    }
  }

  private void validateChallengeDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Les dates de debut et de fin sont obligatoires.");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("La date de debut doit etre avant la date de fin.");
    }
  }

  private void validateFriendshipUsers(Long requesterId, Long addresseeId) {
    if (requesterId == null || addresseeId == null) {
      throw new IllegalArgumentException("Les deux utilisateurs sont obligatoires.");
    }
    if (requesterId.equals(addresseeId)) {
      throw new IllegalArgumentException(
          "Une relation d'amitie doit relier deux utilisateurs differents.");
    }
  }

  private User requireUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + userId));
  }

  private Sport requireSport(Long sportId) {
    return sportRepository
        .findById(sportId)
        .orElseThrow(() -> new IllegalArgumentException("Sport introuvable: " + sportId));
  }

  private Badge requireBadge(Long badgeId) {
    return badgeRepository
        .findById(badgeId)
        .orElseThrow(() -> new IllegalArgumentException("Badge introuvable: " + badgeId));
  }

  private Goal requireGoal(Long goalId) {
    return goalRepository
        .findById(goalId)
        .orElseThrow(() -> new IllegalArgumentException("Objectif introuvable: " + goalId));
  }

  private Workout requireWorkout(Long workoutId) {
    return workoutRepository
        .findById(workoutId)
        .orElseThrow(() -> new IllegalArgumentException("Workout introuvable: " + workoutId));
  }

  private Challenge requireChallenge(Long challengeId) {
    return challengeRepository
        .findById(challengeId)
        .orElseThrow(() -> new IllegalArgumentException("Challenge introuvable: " + challengeId));
  }

  private Friendship requireFriendship(Long friendshipId) {
    return friendshipRepository
        .findById(friendshipId)
        .orElseThrow(
            () -> new IllegalArgumentException("Relation d'amitie introuvable: " + friendshipId));
  }

  private List<Sport> resolveSports(List<Long> sportIds) {
    if (sportIds == null || sportIds.isEmpty()) {
      return new ArrayList<>();
    }
    return sportRepository.findAllById(sportIds);
  }

  private List<Badge> resolveBadges(List<Long> badgeIds) {
    if (badgeIds == null || badgeIds.isEmpty()) {
      return new ArrayList<>();
    }
    return badgeRepository.findAllById(badgeIds);
  }

  private String normalizeIconPath(String iconPath) {
    String normalized = normalizeNullable(iconPath);
    if (normalized == null) {
      return DEFAULT_BADGE_ICON;
    }
    return normalized;
  }

  private void syncGoalMembership(Goal goal, User owner) {
    if (goal.getId() == null || owner.getId() == null) {
      return;
    }

    for (User user : userRepository.findAll()) {
      boolean hasGoal =
          user.getGoals().stream()
              .anyMatch(currentGoal -> goal.getId().equals(currentGoal.getId()));
      boolean shouldHaveGoal = owner.getId().equals(user.getId());

      if (shouldHaveGoal && !hasGoal) {
        user.getGoals().add(goal);
        userRepository.save(user);
      }
      if (!shouldHaveGoal && hasGoal) {
        user.getGoals().removeIf(currentGoal -> goal.getId().equals(currentGoal.getId()));
        userRepository.save(user);
      }
    }
  }

  private void removeGoalFromUsers(Long goalId) {
    for (User user : userRepository.findAll()) {
      boolean changed = user.getGoals().removeIf(currentGoal -> goalId.equals(currentGoal.getId()));
      if (changed) {
        userRepository.save(user);
      }
    }
  }
}
