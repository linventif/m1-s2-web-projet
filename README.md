# m1-s2-web-projet

[![Quality Gate Status](https://sorar.linv.dev/api/project_badges/measure?project=linventif_m1-s2-web-projet_AZ1noB8aXwM5IsPrSN1-&metric=alert_status&token=sqb_069c921777d8739cb8050f2cbb3011b1ebc28bea)](https://sorar.linv.dev/dashboard?id=linventif_m1-s2-web-projet_AZ1noB8aXwM5IsPrSN1-)

[![Coverage](https://sorar.linv.dev/api/project_badges/measure?project=linventif_m1-s2-web-projet_AZ1noB8aXwM5IsPrSN1-&metric=coverage&token=sqb_069c921777d8739cb8050f2cbb3011b1ebc28bea)](https://sorar.linv.dev/dashboard?id=linventif_m1-s2-web-projet_AZ1noB8aXwM5IsPrSN1-)

## Links

- [Sport Flow](https://sportflow.linv.dev)
- [JavaDoc](https://linventif.github.io/m1-s2-web-projet/java-docs/)
- [Swagger UI](https://linventif.github.io/m1-s2-web-projet/swagger/)
- [SonarQube](https://sorar.linv.dev) (user: `indu`, password: `le nom du prof`)
- [GitHub Project](https://github.com/users/linventif/projects/7/views/1)
- [GitHub Repository](https://github.com/linventif/m1-s2-web-projet)

## Team Members

- [Grégoire Launay--Bécue](https://github.com/linventif)
- [Enzo Landrecy](https://github.com/Zolkn-Sama)
- [Robbe Leushuis](https://github.com/Leushuis)
- [Pham-hang269](https://github.com/Pham-hang269)

## Installation

1. Cloner le projet: `git clone git@github.com:linventif/m1-s2-web-projet.git`
2. Se placer dans le dossier du projet: `cd m1-s2-web-projet`
3. Lancer la base de données PostgreSQL avec Docker Compose: `docker compose up -d`
4. Lancer l'application Spring Boot: `mvn spring-boot:run`

## Diagrammes

![Diagramme de classes](./diagram_class.png)

Diagramme de classes représentant les différentes classes du projet et leurs relations.

![Diagramme de Relations](./diagram_relations.png)

Diagramme de classes représentant les différentes classes du projet et leurs relations.

```mermaid
flowchart TD
  A[Code push / PR / tag / manual] --> B{GitHub Actions}

  B --> C[Auto Format<br/>push != main/master]
  C --> C1[spotless:apply + prettier]
  C1 --> C2[commit auto du bot]
  C2 --> C3[clean verify]

  B --> D[CI Sonar<br/>push + pull_request]
  D --> D1[clean verify]
  D1 --> D2[JaCoCo report + upload artifact]
  D2 --> D3[sonar:sonar]

  B --> E[Publish JAR Package<br/>main/master + manual]
  E --> E1[mvn clean deploy -DskipTests]
  E1 --> E2[GitHub Packages Maven]
  E1 --> E3[Upload artifact target/*.jar]

  B --> F[Docker Image<br/>main/master, tags v*, PR, manual]
  F --> F1[buildx + metadata]
  F1 --> F2[Build image]
  F2 --> F3{PR ?}
  F3 -->|Oui| F4[Build only]
  F3 -->|Non| F5[Push GHCR<br/>ghcr.io/<owner>/sportflow]

  B --> G[Publish Javadoc<br/>main/master + manual]
  G --> G1[Generate Javadoc]
  G1 --> G2[Run app + export OpenAPI]
  G2 --> G3[Build Pages artifact]
  G3 --> G4[Deploy GitHub Pages]
```

Diagramme de flux représentant les différentes étapes du pipeline CI/CD mis en place avec GitHub Actions.
