package web.sportflow;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import web.sportflow.openapi.HtmlRedirectApiDoc;
import web.sportflow.openapi.InternalServerErrorApiDoc;

@Tag(name = "Navigation")
@Controller
@InternalServerErrorApiDoc
public class HomeController {

  @Operation(
      summary = "Redirige la racine de l'application vers le dashboard",
      description =
          "Intercepte l'acces a la racine `/` puis redirige vers la route applicative `/dashboard`, qui delegue ensuite vers le tableau de bord utilisateur.")
  @HtmlRedirectApiDoc
  @GetMapping("/")
  public String redirectToDashboardHome() {
    return "redirect:/dashboard";
  }

  @Operation(
      summary = "Redirige vers le tableau de bord utilisateur",
      description =
          "Intercepte la route generique `/dashboard` puis redirige vers `/user/dashboard`, qui correspond au tableau de bord principal de l'espace utilisateur.")
  @HtmlRedirectApiDoc
  @GetMapping("/dashboard")
  public String redirectToDashboard() {
    return "redirect:/user/dashboard";
  }

  @Operation(
      summary = "Corrige la faute de frappe sur l'URL du dashboard",
      description =
          "Capture la variante erronee `/dashbord` puis redirige vers `/dashboard` afin de conserver une navigation tolerante aux fautes de saisie.")
  @HtmlRedirectApiDoc
  @GetMapping("/dashbord")
  public String redirectTypoDashbord() {
    return "redirect:/dashboard";
  }
}
