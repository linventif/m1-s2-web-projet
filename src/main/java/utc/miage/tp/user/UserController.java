package utc.miage.tp.user;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
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
import utc.miage.tp.badge.Badge;
import utc.miage.tp.badge.BadgeService;
import utc.miage.tp.challenge.ChallengeService;
import utc.miage.tp.friendship.Friendship;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.friendship.FriendshipStatus;
import utc.miage.tp.goal.GoalService;
import utc.miage.tp.sport.SportService;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutService;

@Controller
@RequestMapping({"/users", "/user"})
public class UserController {

  private static final Set<String> ALLOWED_AVATAR_EXTENSIONS =
      Set.of("png", "jpg", "jpeg", "webp", "gif");

  @Value("${app.avatar-upload-dir:avatar_upload}")
  private String avatarUploadDir;

  private final UserService userService;
  private final WorkoutService workoutService;
  private final SportService sportService;
  private final GoalService goalService;
  private final ChallengeService challengeService;
  private final BadgeService badgeService;
  private final FriendshipService friendshipService;

  public UserController(
      UserService userService,
      WorkoutService workoutService,
      SportService sportService,
      GoalService goalService,
      ChallengeService challengeService,
      BadgeService badgeService,
      FriendshipService friendshipService) {
    this.userService = userService;
    this.workoutService = workoutService;
    this.sportService = sportService;
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
    model.addAttribute("canEditProfile", true);
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
              model.addAttribute("canEditProfile", false);
              return "user-profile";
            })
        .orElseGet(
            () -> {
              redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur introuvable.");
              return "redirect:/user/users";
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
  public String showAllUsers(@AuthenticationPrincipal User currentUser, Model model) {
    List<User> users =
        userService.getAll().stream()
            .filter(user -> !user.getId().equals(currentUser.getId()))
            .toList();
    model.addAttribute("users", users);
    populateFriendshipContext(currentUser, model);
    return "user-users";
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
  public String showAllFriends(@AuthenticationPrincipal User currentUser, Model model) {
    populateFriendshipContext(currentUser, model);
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
        redirectAttributes.addFlashAttribute("message", "Friend request auto-accepted.");
      } else {
        redirectAttributes.addFlashAttribute("message", "Friend request sent.");
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
      redirectAttributes.addFlashAttribute("message", "Friend request accepted.");
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
      redirectAttributes.addFlashAttribute("message", "Friend request refused.");
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
      redirectAttributes.addFlashAttribute("message", "Friend removed.");
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
    model.addAttribute("unlockedBadgesByWorkoutId", unlockedBadgesByWorkoutId);
    return "user-workout";
  }

  @GetMapping({"/goal", "/goals"})
  public String showGoals(@AuthenticationPrincipal User currentUser, Model model) {
    User goalUser =
        currentUser == null
            ? null
            : userService.getUserById(currentUser.getId()).orElse(currentUser);
    model.addAttribute("user", goalUser);
    model.addAttribute("goals", goalUser == null ? List.of() : goalUser.getGoals());
    return "user-goals";
  }

  @GetMapping("/dashboard")
  public String showDashboard(@AuthenticationPrincipal User currentUser, Model model) {
    model.addAttribute("goals", goalService.getAll());
    model.addAttribute("workouts", workoutService.getAll());
    model.addAttribute("activeChallenges", challengeService.getAll());
    model.addAttribute("badges", badgeService.getAll());
    model.addAttribute("friends", friendshipService.getAcceptedFriendships(currentUser.getId()));
    model.addAttribute("currentMonthLabel", "Avril 2026");
    model.addAttribute("mainGoalLabel", "Objectif : 50 km");
    return "dashboard";
  }

  private void populateUserCreationForm(Model model, User user) {
    model.addAttribute("user", user);
  }

  private void populateProfileView(Model model, User user) {
    Set<Long> unlockedBadgeIds =
        user.getBadges().stream()
            .map(Badge::getId)
            .filter(id -> id != null)
            .collect(Collectors.toSet());
    model.addAttribute("user", user);
    model.addAttribute("allBadges", badgeService.getAll());
    model.addAttribute("unlockedBadgeIds", unlockedBadgeIds);
    model.addAttribute("bmi", userService.calculateBMI(user));
    model.addAttribute("recommendation", userService.getWorkoutRecommendation(user));
    model.addAttribute("bmr", userService.calculateBMR(user));
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
