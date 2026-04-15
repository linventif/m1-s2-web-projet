package web.sportflow.user;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeService;
import web.sportflow.challenge.ChallengeService;
import web.sportflow.friendship.Friendship;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.friendship.FriendshipStatus;
import web.sportflow.goal.GoalService;
import web.sportflow.sport.Sport;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutDashboardDisplay;
import web.sportflow.workout.WorkoutService;

@Controller
@RequestMapping({"/users", "/user"})
public class UserController {

  private static final Set<String> ALLOWED_AVATAR_EXTENSIONS =
      Set.of("png", "jpg", "jpeg", "webp", "gif");

  @Value("${app.avatar-upload-dir:upload_data/images/avatar}")
  private String avatarUploadDir;

  private final UserService userService;
  private final WorkoutService workoutService;
  private final GoalService goalService;
  private final ChallengeService challengeService;
  private final BadgeService badgeService;
  private final FriendshipService friendshipService;

  public UserController(
      UserService userService,
      WorkoutService workoutService,
      GoalService goalService,
      ChallengeService challengeService,
      BadgeService badgeService,
      FriendshipService friendshipService) {
    this.userService = userService;
    this.workoutService = workoutService;
    this.goalService = goalService;
    this.challengeService = challengeService;
    this.badgeService = badgeService;
    this.friendshipService = friendshipService;
  }

  @GetMapping({"", "/"})
  public String showMenu() {
    return "user-menu";
  }

  @GetMapping("/create")
  public String showCreateForm(Model model) {
    populateUserCreationForm(model, new User());
    return "user-create";
  }

  @PostMapping("/create")
  public String createUser(
      @ModelAttribute User user,
      @RequestParam String password,
      @RequestParam String codeStatut,
      @RequestParam(name = "organizedConferenceIds", required = false)
          List<Long> organizedConferenceIds,
      @RequestParam(name = "participatingConferenceIds", required = false)
          List<Long> participatingConferenceIds,
      Model model) {
    try {
      User createdUser =
          userService.createUser(
              user, password, codeStatut, organizedConferenceIds, participatingConferenceIds);
      model.addAttribute(
          "message",
          "Utilisateur ajoute avec succes : "
              + createdUser.getFirstname()
              + " "
              + createdUser.getLastname()
              + ".");
      return "user-list";
    } catch (IllegalArgumentException exception) {
      populateUserCreationForm(model, user);
      model.addAttribute("errorMessage", exception.getMessage());
      return "user-create";
    }
  }

  @GetMapping("/profile")
  public String showProfile(@AuthenticationPrincipal User currentUser, Model model) {
    User profileUser =
        currentUser == null
            ? null
            : userService.getUserById(currentUser.getId()).orElse(currentUser);
    populateProfileView(model, profileUser);
    populateProfileFriendshipContext(currentUser, profileUser, model);
    model.addAttribute("canEditProfile", true);
    model.addAttribute("showOwnGoals", true);
    return "user-profile";
  }

  @GetMapping({"/profile/{userId:[0-9]+}", "/{userId:[0-9]+}/profile"})
  public String showUserProfile(
      @AuthenticationPrincipal User currentUser,
      @PathVariable Long userId,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (currentUser != null && currentUser.getId() != null && currentUser.getId().equals(userId)) {
      return "redirect:/user/profile";
    }

    return userService
        .getUserById(userId)
        .map(
            user -> {
              populateProfileView(model, user);
              populateProfileFriendshipContext(currentUser, user, model);
              model.addAttribute("canEditProfile", false);
              model.addAttribute("showOwnGoals", false);
              return "user-profile";
            })
        .orElseGet(
            () -> {
              redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable.");
              return "redirect:/user/friends";
            });
  }

  @GetMapping("/profile/edit")
  public String showEditProfile(@AuthenticationPrincipal User currentUser, Model model) {
    populateProfileEditForm(model, currentUser);
    return "user-profile-edit";
  }

  @PostMapping("/profile/edit")
  public String updateProfile(
      @AuthenticationPrincipal User currentUser,
      @RequestParam String firstname,
      @RequestParam String lastname,
      @RequestParam String email,
      @RequestParam Double weight,
      @RequestParam Double height,
      @RequestParam Sex sex,
      @RequestParam PracticeLevel level,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate birthDate,
      @RequestParam(name = "avatarFile", required = false) MultipartFile avatarFile,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      User updatedUser =
          userService.updateCurrentUserProfile(
              currentUser, firstname, lastname, email, weight, height, sex, birthDate, level);
      if (avatarFile != null && !avatarFile.isEmpty()) {
        String avatarPath = storeAvatarForUser(updatedUser.getId(), avatarFile);
        String avatarPathWithVersion = avatarPath + "?v=" + System.currentTimeMillis();
        updatedUser.setProfileImagePath(avatarPathWithVersion);
        currentUser.setProfileImagePath(avatarPathWithVersion);
        userService.save(updatedUser);
      }
      redirectAttributes.addFlashAttribute("message", "Profil mis à jour avec succès.");
      return "redirect:/user/profile";
    } catch (Exception e) {
      currentUser.setFirstname(firstname);
      currentUser.setLastname(lastname);
      currentUser.setEmail(email);
      currentUser.setWeight(weight);
      currentUser.setHeight(height);
      currentUser.setSex(sex);
      currentUser.setBirthDate(birthDate);
      currentUser.setLevel(level);
      populateProfileEditForm(model, currentUser);
      model.addAttribute("errorMessage", e.getMessage());
      return "user-profile-edit";
    }
  }

  private String storeAvatarForUser(Long userId, MultipartFile avatarFile) throws IOException {
    if (userId == null) {
      throw new IllegalArgumentException("Impossible de sauvegarder l'avatar.");
    }
    if (avatarFile.getContentType() == null || !avatarFile.getContentType().startsWith("image/")) {
      throw new IllegalArgumentException("Le fichier doit etre une image.");
    }

    String extension = extractExtension(avatarFile.getOriginalFilename());
    if (!ALLOWED_AVATAR_EXTENSIONS.contains(extension)) {
      throw new IllegalArgumentException("Format d'image non supporte.");
    }

    Path uploadDir = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
    Files.createDirectories(uploadDir);
    cleanupExistingUserAvatars(uploadDir, userId);

    String fileName = "user_" + userId + "." + extension;
    Path targetPath = uploadDir.resolve(fileName).normalize();
    if (!targetPath.startsWith(uploadDir)) {
      throw new IllegalArgumentException("Nom de fichier invalide.");
    }

    try (InputStream inputStream = avatarFile.getInputStream()) {
      Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }
    return "/avatar_upload/" + fileName;
  }

  private String extractExtension(String originalFilename) {
    if (originalFilename == null || !originalFilename.contains(".")) {
      return "";
    }
    int lastDotIndex = originalFilename.lastIndexOf('.');
    if (lastDotIndex == originalFilename.length() - 1) {
      return "";
    }
    return originalFilename.substring(lastDotIndex + 1).toLowerCase(Locale.ROOT);
  }

  private void cleanupExistingUserAvatars(Path uploadDir, Long userId) throws IOException {
    String pattern = "user_" + userId + ".*";
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, pattern)) {
      for (Path path : stream) {
        Files.deleteIfExists(path);
      }
    }
  }

  @GetMapping("/users")
  public String redirectUsersPage() {
    return "redirect:/users/friends";
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute RegistrationDTO registrationDTO, Model model) {
    try {
      userService.registerUser(registrationDTO);
      model.addAttribute("message", "Compte créé avec succès !");
      return "user-login";
    } catch (Exception e) {
      model.addAttribute("errorMessage", e.getMessage());
      return "user-create";
    }
  }

  @GetMapping("/friends")
  public String manageFriends(
      @AuthenticationPrincipal User currentUser,
      @RequestParam(value = "q", required = false) String query,
      @PageableDefault(size = 10, sort = "lastname") Pageable pageable,
      Model model) {
    populateFriendshipContext(currentUser, model);

    Page<User> userPage;
    if (query != null && !query.trim().isEmpty()) {
      userPage = userService.searchUsers(query, pageable);
    } else {
      userPage = userService.getAll(pageable);
    }
    model.addAttribute("userPage", userPage);
    model.addAttribute("query", query);
    return "user-friends";
  }

  private void populateFriendshipContext(User currentUser, Model model) {
    List<Friendship> incomingRequests =
        friendshipService.getIncomingPendingRequests(currentUser.getId());
    List<Friendship> outgoingRequests =
        friendshipService.getOutgoingPendingRequests(currentUser.getId());
    List<Friendship> acceptedFriendships =
        friendshipService.getAcceptedFriendships(currentUser.getId());

    Map<Long, Long> incomingRequestIdByUserId =
        incomingRequests.stream()
            .collect(
                Collectors.toMap(
                    friendship -> friendship.getRequester().getId(),
                    Friendship::getId,
                    (first, ignored) -> first));

    Set<Long> incomingRequestSenderIds =
        incomingRequests.stream()
            .map(friendship -> friendship.getRequester().getId())
            .collect(Collectors.toSet());
    Set<Long> outgoingRequestTargetIds =
        outgoingRequests.stream()
            .map(friendship -> friendship.getAddressee().getId())
            .collect(Collectors.toSet());
    Set<Long> friendIds =
        acceptedFriendships.stream()
            .map(
                friendship ->
                    friendship.getRequester().getId().equals(currentUser.getId())
                        ? friendship.getAddressee().getId()
                        : friendship.getRequester().getId())
            .collect(Collectors.toSet());

    model.addAttribute("incomingRequests", incomingRequests);
    model.addAttribute("outgoingRequests", outgoingRequests);
    model.addAttribute("acceptedFriendships", acceptedFriendships);
    model.addAttribute("incomingRequestIdByUserId", incomingRequestIdByUserId);
    model.addAttribute("incomingRequestSenderIds", incomingRequestSenderIds);
    model.addAttribute("outgoingRequestTargetIds", outgoingRequestTargetIds);
    model.addAttribute("friendIds", friendIds);
    model.addAttribute("currentUserId", currentUser.getId());
  }

  @PostMapping("/friends/request")
  public String sendFriendRequest(
      @AuthenticationPrincipal User currentUser,
      @RequestParam Long targetUserId,
      @RequestParam(defaultValue = "/users/friends") String returnTo,
      RedirectAttributes redirectAttributes) {
    try {
      Friendship friendship = friendshipService.sendRequest(currentUser.getId(), targetUserId);
      if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
        redirectAttributes.addFlashAttribute("message", "Demande d'ami acceptée automatiquement.");
      } else {
        redirectAttributes.addFlashAttribute("message", "Demande d'ami envoyée.");
      }
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }
    return "redirect:" + resolveReturnTo(returnTo);
  }

  @PostMapping("/friends/accept")
  public String acceptFriendRequest(
      @AuthenticationPrincipal User currentUser,
      @RequestParam Long friendshipId,
      @RequestParam(defaultValue = "/users/friends") String returnTo,
      RedirectAttributes redirectAttributes) {
    try {
      friendshipService.acceptRequest(currentUser.getId(), friendshipId);
      redirectAttributes.addFlashAttribute("message", "Demande d'ami acceptée.");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }
    return "redirect:" + resolveReturnTo(returnTo);
  }

  @PostMapping("/friends/refuse")
  public String refuseFriendRequest(
      @AuthenticationPrincipal User currentUser,
      @RequestParam Long friendshipId,
      @RequestParam(defaultValue = "/users/friends") String returnTo,
      RedirectAttributes redirectAttributes) {
    try {
      friendshipService.refuseRequest(currentUser.getId(), friendshipId);
      redirectAttributes.addFlashAttribute("message", "Demande d'ami refusée.");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }
    return "redirect:" + resolveReturnTo(returnTo);
  }

  @PostMapping("/friends/unfriend")
  public String unfriend(
      @AuthenticationPrincipal User currentUser,
      @RequestParam Long friendId,
      @RequestParam(defaultValue = "/users/friends") String returnTo,
      RedirectAttributes redirectAttributes) {
    try {
      friendshipService.unfriend(currentUser.getId(), friendId);
      redirectAttributes.addFlashAttribute("message", "Ami retiré.");
    } catch (IllegalArgumentException exception) {
      redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
    }
    return "redirect:" + resolveReturnTo(returnTo);
  }

  private String resolveReturnTo(String returnTo) {
    if (returnTo != null && (returnTo.startsWith("/users/") || returnTo.startsWith("/user/"))) {
      return returnTo;
    }
    return "/users/friends";
  }

  @GetMapping("/workout")
  public String showWorkout(Model model) {
    List<Workout> workouts = workoutService.getAll();
    Map<Long, List<Badge>> unlockedBadgesByWorkoutId = new HashMap<>();
    for (Workout workout : workouts) {
      unlockedBadgesByWorkoutId.put(workout.getId(), getUnlockedBadgesForWorkout(workout));
    }
    model.addAttribute("workouts", workouts);
    model.addAttribute(
        "workoutDisplays", workouts.stream().map(WorkoutDashboardDisplay::new).toList());
    model.addAttribute("unlockedBadgesByWorkoutId", unlockedBadgesByWorkoutId);
    return "user-workout";
  }

  @GetMapping({"/goal", "/goals"})
  public String redirectGoalsPage() {
    return "redirect:/users/profile#goals";
  }

  @GetMapping("/dashboard")
  public String showDashboard(@AuthenticationPrincipal User currentUser, Model model) {
    double totalDistanceThisWeek = workoutService.getTotalDistanceThisWeek(currentUser);
    double totalDurationThisWeek = workoutService.getTotalDurationThisWeek(currentUser);
    double totalCaloriesThisWeek = workoutService.getTotalCaloriesThisWeek(currentUser);

    int todayIndex = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;
    int totalMinutes = (int) Math.round(totalDurationThisWeek);
    int hoursPart = totalMinutes / 60;
    int minutesPart = totalMinutes % 60;

    List<Friendship> acceptedFriendships =
        currentUser != null && currentUser.getId() != null
            ? friendshipService.getAcceptedFriendships(currentUser.getId())
            : List.of();
    List<Workout> visibleWorkouts =
        currentUser != null && currentUser.getId() != null
            ? workoutService.getFriendsWorkout(currentUser.getId())
            : List.of();

    model.addAttribute(
        "goals",
        currentUser != null && currentUser.getId() != null
            ? goalService.getFriendsAndUserGoal(currentUser)
            : List.of());
    model.addAttribute("workouts", visibleWorkouts);

    model.addAttribute(
        "workoutDisplays", visibleWorkouts.stream().map(WorkoutDashboardDisplay::new).toList());
    model.addAttribute(
        "activeChallenges",
        currentUser != null && currentUser.getId() != null
            ? challengeService.getFriendsAndUserChallenge(currentUser)
            : List.of());
    model.addAttribute("friends", acceptedFriendships);

    model.addAttribute("currentMonthLabel", "Avril 2026");
    model.addAttribute("mainGoalLabel", "Objectif : 50 km");

    model.addAttribute("totalDistanceThisWeek", Math.round(totalDistanceThisWeek * 10.0) / 10.0);
    model.addAttribute("todayIndex", todayIndex);
    model.addAttribute("hoursPart", hoursPart);
    model.addAttribute("minutesPart", minutesPart);
    model.addAttribute("totalCaloriesThisWeek", Math.round(totalCaloriesThisWeek));

    return "dashboard";
  }

  @GetMapping("/statistique")
  public String showStatistiquePage(@AuthenticationPrincipal User currentUser, Model model) {
    double distanceThisWeek = workoutService.getTotalDistanceThisWeek(currentUser);
    double distanceThisMonth = workoutService.getTotalDistanceThisMonth(currentUser);
    double distanceThisYear = workoutService.getTotalDistanceThisYear(currentUser);
    double totalDurationThisWeek = workoutService.getTotalDurationThisWeek(currentUser);
    double totalCaloriesThisWeek = workoutService.getTotalCaloriesThisWeek(currentUser);

    int todayIndex = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;
    int totalMinutes = (int) Math.round(totalDurationThisWeek);
    int hoursPart = totalMinutes / 60;
    int minutesPart = totalMinutes % 60;

    double averageMonthlyDistanceThisYear =
        workoutService.getAverageMonthlyDistanceThisYear(currentUser);

    double distanceGapVsAverage = workoutService.getDistanceGapVsAverageMonthly(currentUser);

    int currentDayIndex = java.time.LocalDate.now().getDayOfWeek().getValue() - 1;
    int currentDayOfMonthIndex = java.time.LocalDate.now().getDayOfMonth() - 1;
    int currentMonthIndex = java.time.LocalDate.now().getMonthValue() - 1;

    model.addAttribute("currentDayIndex", currentDayIndex);
    model.addAttribute("currentDayOfMonthIndex", currentDayOfMonthIndex);
    model.addAttribute("currentMonthIndex", currentMonthIndex);

    model.addAttribute("distanceThisWeek", Math.round(distanceThisWeek * 10.0) / 10.0);
    model.addAttribute("distanceThisMonth", Math.round(distanceThisMonth * 10.0) / 10.0);
    model.addAttribute("distanceThisYear", Math.round(distanceThisYear * 10.0) / 10.0);
    model.addAttribute("todayIndex", todayIndex);
    model.addAttribute("hoursPart", hoursPart);
    model.addAttribute("minutesPart", minutesPart);
    model.addAttribute("totalCaloriesThisWeek", Math.round(totalCaloriesThisWeek));
    model.addAttribute("monthlyBars", workoutService.getMonthlyBarViewsCurrentYear(currentUser));

    model.addAttribute(
        "averageMonthlyDistanceThisYear", Math.round(averageMonthlyDistanceThisYear * 10.0) / 10.0);

    model.addAttribute("distanceGapVsAverage", Math.round(distanceGapVsAverage * 10.0) / 10.0);

    model.addAttribute("monthDayLabels", workoutService.getMonthDayLabels());
    model.addAttribute("currentMonthCurve", workoutService.getCurrentMonthCurve(currentUser));
    model.addAttribute("yearAverageCurve", workoutService.getYearAverageCurve(currentUser));
    model.addAttribute("weekLabels", workoutService.getWeekLabels());
    model.addAttribute("weekDistances", workoutService.getWeekDistances(currentUser));

    model.addAttribute("monthChartLabels", workoutService.getMonthLabelsForChart());
    model.addAttribute(
        "monthChartDistances", workoutService.getMonthDistancesForChart(currentUser));

    model.addAttribute("yearChartLabels", workoutService.getYearLabelsForChart());
    model.addAttribute("yearChartDistances", workoutService.getYearDistancesForChart(currentUser));

    return "user-statistique";
  }

  private void populateUserCreationForm(Model model, User user) {
    model.addAttribute("user", user);
  }

  private void populateProfileView(Model model, User user) {
    model.addAttribute("user", user);
    Set<Long> unlockedBadgeIds =
        user.getBadges().stream()
            .map(Badge::getId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
    model.addAttribute("user", user);
    model.addAttribute("profileGoals", user.getGoals());
    model.addAttribute("profileSports", resolveProfileSports(user));
    model.addAttribute("allBadges", badgeService.getAll());
    model.addAttribute("unlockedBadgeIds", unlockedBadgeIds);
    model.addAttribute("bmi", userService.calculateBMI(user));
    model.addAttribute("recommendation", userService.getWorkoutRecommendation(user));
    model.addAttribute("bmr", userService.calculateBMR(user));
  }

  private void populateProfileFriendshipContext(User currentUser, User profileUser, Model model) {
    model.addAttribute("isOwnProfile", false);
    model.addAttribute("friendshipStatusLabel", "Aucun lien");
    model.addAttribute("friendshipBadgeClass", "badge-ghost");
    model.addAttribute("canSendFriendRequest", false);
    model.addAttribute("canAcceptFriendRequest", false);
    model.addAttribute("canRefuseFriendRequest", false);
    model.addAttribute("canUnfriend", false);
    model.addAttribute("incomingFriendshipId", null);

    if (profileUser == null || profileUser.getId() == null) {
      return;
    }
    if (currentUser == null || currentUser.getId() == null) {
      return;
    }

    Long currentUserId = currentUser.getId();
    Long profileUserId = profileUser.getId();

    if (currentUserId.equals(profileUserId)) {
      model.addAttribute("isOwnProfile", true);
      model.addAttribute("friendshipStatusLabel", "Votre profil");
      model.addAttribute("friendshipBadgeClass", "badge-primary");
      return;
    }

    Friendship relationship =
        friendshipService.findRelationshipBetween(currentUserId, profileUserId).orElse(null);

    if (relationship == null) {
      model.addAttribute("canSendFriendRequest", true);
      return;
    }

    switch (relationship.getStatus()) {
      case ACCEPTED -> {
        model.addAttribute("friendshipStatusLabel", "Amis");
        model.addAttribute("friendshipBadgeClass", "badge-success");
        model.addAttribute("canUnfriend", true);
      }
      case PENDING -> {
        boolean requestSentByCurrentUser =
            relationship.getRequester() != null
                && currentUserId.equals(relationship.getRequester().getId());

        if (requestSentByCurrentUser) {
          model.addAttribute("friendshipStatusLabel", "Demande envoyee");
          model.addAttribute("friendshipBadgeClass", "badge-warning");
        } else {
          model.addAttribute("friendshipStatusLabel", "Demande recue");
          model.addAttribute("friendshipBadgeClass", "badge-info");
          model.addAttribute("canAcceptFriendRequest", true);
          model.addAttribute("canRefuseFriendRequest", true);
          model.addAttribute("incomingFriendshipId", relationship.getId());
        }
      }
      case REFUSED -> {
        model.addAttribute("friendshipStatusLabel", "Demande refusee");
        model.addAttribute("friendshipBadgeClass", "badge-error");
        model.addAttribute("canSendFriendRequest", true);
      }
    }
  }

  private List<Sport> resolveProfileSports(User user) {
    if (user == null || user.getId() == null) {
      return List.of();
    }

    Map<Long, Sport> sportsById = new LinkedHashMap<>();
    for (Workout workout : workoutService.getAll()) {
      if (workout.getUser() == null
          || workout.getUser().getId() == null
          || !user.getId().equals(workout.getUser().getId())
          || workout.getSport() == null) {
        continue;
      }
      Sport sport = workout.getSport();
      if (sport.getId() == null) {
        continue;
      }
      sportsById.putIfAbsent(sport.getId(), sport);
    }
    return List.copyOf(sportsById.values());
  }

  private List<Badge> getUnlockedBadgesForWorkout(Workout workout) {
    if (workout == null
        || workout.getUser() == null
        || workout.getUser().getBadges() == null
        || workout.getSport() == null
        || workout.getSport().getName() == null) {
      return Collections.emptyList();
    }

    String sportPrefix = workout.getSport().getName() + " - ";
    return workout.getUser().getBadges().stream()
        .filter(badge -> badge.getName() != null && badge.getName().startsWith(sportPrefix))
        .toList();
  }

  private void populateProfileEditForm(Model model, User user) {
    model.addAttribute("user", user);
    model.addAttribute("sexes", Sex.values());
    model.addAttribute("practiceLevels", PracticeLevel.values());
  }
}
