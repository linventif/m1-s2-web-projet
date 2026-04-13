package utc.miage.tp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String redirectToProfile() {
    return "redirect:/user/profile";
  }

  @GetMapping("/dashboard")
  public String redirectToDashboard() {
    return "redirect:/user/dashboard";
  }

  @GetMapping("/dashbord")
  public String redirectTypoDashbord() {
    return "redirect:/dashboard";
  }
}
