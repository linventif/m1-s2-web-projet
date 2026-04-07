# m1-s2-web-projet

[![Quality Gate Status](https://sorar.linv.dev/api/project_badges/measure?project=linventif_m1-s2-indu_AZzXDvGQXwM5IsPrSNyK&metric=alert_status&token=sqb_d87853229bc05d9b5304356dabb2d0890a21ff66)](https://sorar.linv.dev/dashboard?id=linventif_m1-s2-indu_AZzXDvGQXwM5IsPrSNyK)

[![Coverage](https://sorar.linv.dev/api/project_badges/measure?project=linventif_m1-s2-indu_AZzXDvGQXwM5IsPrSNyK&metric=coverage&token=sqb_d87853229bc05d9b5304356dabb2d0890a21ff66)](https://sorar.linv.dev/dashboard?id=linventif_m1-s2-indu_AZzXDvGQXwM5IsPrSNyK)

## Links

- [JavaDoc](https://linventif.github.io/m1-s2-indu)
- [SonarQube](https://sorar.linv.dev) (user: `indu`, password: `le nom du prof`)
- [GitHub Project](https://github.com/users/linventif/projects/6/views/1)
- [GitHub Repository](https://github.com/linventif/m1-s2-indu)

## Team Members

- [Grégoire Launay--Bécue](https://github.com/linventif)
- [Enzo Landrecy](https://github.com/Zolkn-Sama)
- [Robbe Leushuis](https://github.com/Leushuis)
- [Pham-hang269](https://github.com/Pham-hang269)


# Site de suivi sportif

Les features principales de l’application incluent (mais ne sont pas limitées à) :
  - L’utilisateur peut se créer (et modifier) un profil sur le site avec ses préférences en
termes de sport, son niveau de pratique, ses informations personnelles (sexe, âge,
taille, poids…) et ses objectifs personnels (par ex. « courir 50 km par mois » … )
  - L’utilisateur pourra enregistrer des activités avec le type de sport, la date, la durée, la
distance et une évaluation.
  - Pour chaque activité, les calories consommées seront automatiquement calculées.
De plus, les conditions météo seront automatiquement récupérées depuis une API
externe et gratuite (par exemple open-météo…).
  - Dashboard personnel, afin de visualiser toutes ses activités, ses progrès au cours du
temps, ses performances par rapport à ses objectifs (sous forme d’indicateurs ou de
courbes).
  - Chaque utilisateur pourra rechercher un autre utilisateur et devenir ami avec lui/elle.
Il aura ainsi un ensemble d’amis, dont il pourra voir les activités.
  - Chaque utilisateur pourra rechercher un autre utilisateur et devenir ami avec lui/elle.
Il aura ainsi un ensemble d’amis, dont il pourra voir les activités.
  - Challenges : un utilisateur pourra créer ou rejoindre des challenges (par ex. «
réaliser le plus de pompes possibles par jour », « courir le plus longtemps dans une
semaine » …). Un challenge aura une durée de validité. Pour chaque challenge, le
classement des différents participants pourra être affiché.
  - Badges : à chaque accomplissement, l’utilisateur pourra gagner des badges (par ex.
Premier 5km, 10km, 21km, 42km … de course …), qui seront affichés avec son
profil.
  - Commentaires et réactions. Un utilisateur pourra réagir aux activités de ses amis au
moyen d’un commentaire ou d’une réaction (kudos).

# Les technos utilisées

Backend :
  - Spring boot (Java)
      - Thymeleaf (Front)
      - Spring web
      - JPA
      - Spring dev-tools
      - Spring boot security
   
Frontend :
  - Thymeleaf
  - Daisy UI
