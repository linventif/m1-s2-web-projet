package web.sportflow;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Navigation")
@Controller
public class HomeController {

  @Operation(
      summary = "Redirige la racine de l'application vers le dashboard",
      description =
          "Intercepte l'acces a la racine `/` puis redirige vers la route applicative `/dashboard`, qui delegue ensuite vers le tableau de bord utilisateur.")
  @ApiResponse(
      responseCode = "302",
      description = "Redirection vers /dashboard",
      content =
          @Content(
              mediaType = "text/html",
              examples = @ExampleObject(value = "redirect:/dashboard")))
  @ApiResponse(
      responseCode = "500",
      description = "Erreur interne lors de la redirection depuis la racine",
      content = @Content)
  @GetMapping("/")
  public String redirectToDashboardHome() {
    return "redirect:/dashboard";
  }

  @Operation(
      summary = "Redirige vers le tableau de bord utilisateur",
      description =
          "Intercepte la route generique `/dashboard` puis redirige vers `/user/dashboard`, qui correspond au tableau de bord principal de l'espace utilisateur.")
  @ApiResponse(
        responseCode = "302",
        description = "Redirection vers /user/dashboard",
        content =
            @Content(
                mediaType = "text/html",
                examples = @ExampleObject(value = "redirect:/user/dashboard")))
  @ApiResponse(
        responseCode = "500",
        description = "Erreur interne lors de la redirection vers le dashboard utilisateur",
        content = @Content)
  @GetMapping("/dashboard")
  public String redirectToDashboard() {
    return "redirect:/user/dashboard";
  }

  @Operation(
      summary = "Corrige la faute de frappe sur l'URL du dashboard",
      description =
          "Capture la variante erronee `/dashbord` puis redirige vers `/dashboard` afin de conserver une navigation tolerante aux fautes de saisie.")
  @ApiResponse(
        responseCode = "302",
        description = "Redirection vers /dashboard",
        content =
            @Content(
                mediaType = "text/html",
                examples = @ExampleObject(value = "redirect:/dashboard")))
  @ApiResponse(
        responseCode = "500",
        description = "Erreur interne lors de la redirection de l'URL mal orthographiee",
        content = @Content)
  @GetMapping("/dashbord")
  public String redirectTypoDashbord() {
    return "redirect:/dashboard";
  }
}
