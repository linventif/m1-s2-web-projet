package utc.miage.tp.user;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utc.miage.tp.workout.WorkoutService;

@Controller
@RequestMapping("/users")
public class UserController {

  private final WorkoutService workoutService;
  private final UserService userService;

  public UserController(UserService userService, WorkoutService workoutService) {
    this.userService = userService;
    this.workoutService = workoutService;
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
          "message", "Utilisateur ajoute avec succes : " + createdUser.getName() + ".");
      return "user-list";
    } catch (IllegalArgumentException exception) {
      populateUserCreationForm(model, user);
      model.addAttribute("errorMessage", exception.getMessage());
      return "user-create";
    }
  }

  @GetMapping("/profile")
  public String showProfile(@AuthenticationPrincipal UserDetails currentUser, Model model) {
    model.addAttribute("user", currentUser);
    return "user-profile";
  }

  @GetMapping("/users")
  public String showAllUsers(Model model) {
    model.addAttribute("users", userService.getAllUser());
    return "user-users";
  }

  @GetMapping("/friends")
  public String showAllFriends(Model model) {
    model.addAttribute("users", userService.getAllUser());
    return "user-friends";
  }

  @GetMapping("/workout")
  public String showWorkout(Model model) {
    model.addAttribute("workout", workoutService.getAllWorkout());
    return "user-workout";
  }

  // @GetMapping("/myfriends")
  // public String getMethodName(HttpSession session, Model model) {
  // Object loggedUserId = session.getAttribute("loggedUserId");
  // if (!(loggedUserId instanceof Long userId)) {
  // return "redirect:/users/login";
  // }
  // return userService.getUserById(userId)
  // .map(user -> {
  // model.addAttribute("user", user);
  // return "user-profile";
  // })
  // .orElseGet(() -> {
  // session.invalidate();
  // return "redirect:/users/login";
  // });
  // }

  // @PostMapping("/friends/add")
  // public String postMethodName(@RequestBody String entity) {
  // // TODO: process POST request

  // return entity;
  // }

  private void populateUserCreationForm(Model model, User user) {
    model.addAttribute("user", user);
  }
}
