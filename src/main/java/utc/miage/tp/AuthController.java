package utc.miage.tp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import utc.miage.tp.user.RegistrationDTO;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserService;

@Controller
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new User());
    return "register"; // Points to templates/register.html
  }

  // 2. Process Registration
  @PostMapping("/register")
  public String registerUser(@ModelAttribute("user") RegistrationDTO registrationDTO) {
    userService.registerUser(registrationDTO);
    return "redirect:/login?success"; // Send them to login after signing up
  }

  // 3. Show Custom Login Page
  @GetMapping("/login")
  public String login() {
    return "login"; // Points to templates/login.html
  }
}
