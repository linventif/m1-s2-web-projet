package web.sportflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import web.sportflow.openapi.BadRequestApiDoc;
import web.sportflow.openapi.HtmlRedirectApiDoc;
import web.sportflow.openapi.HtmlViewApiDoc;
import web.sportflow.openapi.InternalServerErrorApiDoc;
import web.sportflow.user.RegistrationDTO;
import web.sportflow.user.User;
import web.sportflow.user.UserService;

@Tag(name = "Authentification")
@Controller
@InternalServerErrorApiDoc
public class AuthController {

  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @Operation(
      summary = "Affiche le formulaire d'inscription",
      description =
          "Retourne la vue HTML du formulaire d'inscription publique. "
              + "Le modele est initialise avec un objet utilisateur vide pour le binding du formulaire.")
  @HtmlViewApiDoc
  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new User());
    return "register"; // Points to templates/register.html
  }

  @Operation(
      summary = "Traite l'inscription d'un nouvel utilisateur",
      description =
          "Enregistre un nouveau compte a partir du DTO d'inscription puis redirige vers la page de connexion avec un indicateur de succes. "
              + "L'operation peut echouer en cas de donnees invalides ou de contraintes metier non respectees.")
  @HtmlRedirectApiDoc
  @BadRequestApiDoc
  @PostMapping("/register")
  public String registerUser(@ModelAttribute("user") RegistrationDTO registrationDTO) {
    userService.registerUser(registrationDTO);
    return "redirect:/login?success"; // Send them to login after signing up
  }

  @Operation(
      summary = "Affiche la page de connexion",
      description = "Retourne la vue HTML de connexion personnalisee de l'application.")
  @HtmlViewApiDoc
  @GetMapping("/login")
  public String login() {
    return "login"; // Points to templates/login.html
  }
}
