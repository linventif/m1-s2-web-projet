package web.sportflow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import web.sportflow.user.RegistrationDTO;
import web.sportflow.user.User;
import web.sportflow.user.UserService;

@Tag(name = "Authentification")
@Controller
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
  @ApiResponse(
        responseCode = "200",
        description = "Vue HTML du formulaire d'inscription",
        content =
            @Content(
                mediaType = "text/html",
                examples =
                    @ExampleObject(value = "<html><body><h1>Inscription</h1></body></html>")))
  @ApiResponse(
        responseCode = "500",
        description = "Erreur interne lors du chargement du formulaire d'inscription",
        content = @Content)
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
  @ApiResponse(
        responseCode = "302",
        description = "Redirection vers /login?success apres inscription reussie",
        content =
            @Content(
                mediaType = "text/html",
                examples = @ExampleObject(value = "redirect:/login?success")))
  @ApiResponse(
        responseCode = "400",
        description = "Donnees d'inscription invalides ou compte deja existant",
        content = @Content)
  @ApiResponse(
        responseCode = "500",
        description = "Erreur interne lors du traitement de l'inscription",
        content = @Content)
  @PostMapping("/register")
  public String registerUser(@ModelAttribute("user") RegistrationDTO registrationDTO) {
    userService.registerUser(registrationDTO);
    return "redirect:/login?success"; // Send them to login after signing up
  }

  @Operation(
      summary = "Affiche la page de connexion",
      description =
          "Retourne la vue HTML de connexion personnalisee de l'application.")
  @ApiResponse(
        responseCode = "200",
        description = "Vue HTML de connexion",
        content =
            @Content(
                mediaType = "text/html",
                examples =
                    @ExampleObject(value = "<html><body><h1>Connexion</h1></body></html>")))
  @ApiResponse(
        responseCode = "500",
        description = "Erreur interne lors du chargement de la page de connexion",
        content = @Content)
  @GetMapping("/login")
  public String login() {
    return "login"; // Points to templates/login.html
  }
}
