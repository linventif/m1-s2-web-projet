package utc.miage.tp.user;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utc.miage.tp.friendship.Friendship;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.friendship.FriendshipStatus;
import utc.miage.tp.goal.GoalService;
import utc.miage.tp.sport.SportService;
import utc.miage.tp.workout.WorkoutService;
import utc.miage.tp.challenge.ChallengeService;
import utc.miage.tp.badge.BadgeService;

@Controller
@RequestMapping("/users")
public class UserController {

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
    model.addAttribute("user", currentUser);
    model.addAttribute("bmi", userService.calculateBMI(currentUser));
    model.addAttribute("recommendation", userService.getWorkoutRecommendation(currentUser));
    model.addAttribute("bmr", userService.calculateBMR(currentUser));
    return "user-profile";
  }

  @GetMapping("/profile/edit")
  public String showEditProfile(@AuthenticationPrincipal User currentUser, Model model) {
    model.addAttribute("user", currentUser);
    return "user-profile-edit";
  }

  @PostMapping("/profile/edit")
  public String updateProfile(
      @AuthenticationPrincipal User currentUser,
      @RequestParam Double weight,
      @RequestParam Double height,
      @RequestParam Sex sex,
      @RequestParam Integer age,
      Model model,
      RedirectAttributes redirectAttributes) {
    try {
      currentUser.setWeight(weight);
      currentUser.setHeight(height);
      currentUser.setSex(sex);
      // Calculer birthDate à partir de l'âge
      java.time.LocalDate birthDate = java.time.LocalDate.now().minusYears(age);
      currentUser.setBirthDate(birthDate);
      userService.save(currentUser);
      redirectAttributes.addFlashAttribute("message", "Profil mis à jour avec succès.");
      return "redirect:/users/profile";
    } catch (Exception e) {
      model.addAttribute("user", currentUser);
      model.addAttribute("errorMessage", e.getMessage());
      return "user-profile-edit";
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
    if (returnTo != null && returnTo.startsWith("/users/")) {
      return returnTo;
    }
    return "/users/friends";
  }

  @GetMapping("/workout")
  public String showWorkout(Model model) {
    model.addAttribute("workouts", workoutService.getAll());
    return "user-workout";
  }

  @GetMapping("/dashboard")
  public String showDashboard( @AuthenticationPrincipal User currentUser, Model model) {
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
}