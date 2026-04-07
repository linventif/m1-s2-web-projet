package utc.miage.tp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

  @GetMapping("/")
  public String redirectToProfile() {
    return "redirect:/users/profile";
  }
}
