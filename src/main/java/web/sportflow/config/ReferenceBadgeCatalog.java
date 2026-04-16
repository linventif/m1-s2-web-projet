package web.sportflow.config;

import java.util.List;
import java.util.Map;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.sport.Sport;
import web.sportflow.user.User;

final class ReferenceBadgeCatalog {

  private static final List<BadgeDto> BADGES =
      List.of(
          new BadgeDto(
              "rookie5k",
              "Course",
              "Rookie 5K",
              "Reussir 5 km cumules en %s.",
              "/images/badge/running_5km.png"),
          new BadgeDto(
              "marathonHerbe",
              "Course",
              "Marathonien en Herbe",
              "Cumuler 42 km en %s.",
              "/images/badge/running_42km.png"),
          new BadgeDto(
              "marathonien",
              "Course",
              "Marathonien",
              "Cumuler 42 km en %s en moins de 4h.",
              "/images/badge/running_42km.png"),
          new BadgeDto(
              "natation",
              "Natation",
              "Ondes Maitrisees",
              "Completer 3 seances de %s en une semaine.",
              "/images/badge/natation.png"),
          new BadgeDto(
              "cyclisme",
              "Cyclisme",
              "Rouleur Urbain",
              "Atteindre 30 km en %s.",
              "/images/badge/cyclisme.png"),
          new BadgeDto(
              "escalade",
              "Escalade",
              "Bloc Determination",
              "Valider 5 sessions de %s.",
              "/images/badge/escalade.png"),
          new BadgeDto(
              "yoga",
              "Yoga",
              "Souplesse Focus",
              "Maintenir 20 minutes de %s sans interruption.",
              "/images/badge/yoga.png"),
          new BadgeDto(
              "musculation",
              "Musculation",
              "Force Reguliere",
              "Terminer 4 seances de %s dans le mois.",
              "/images/badge/yoga.png"),
          new BadgeDto(
              "basketball",
              "Basketball",
              "Adresse Collective",
              "Cumuler 90 minutes de %s avec ballon.",
              "/images/badge/running_5km.png"),
          new BadgeDto(
              "football",
              "Football",
              "Pressing Continu",
              "Cumuler 10 km d'efforts en %s.",
              "/images/badge/running_42km.png"),
          new BadgeDto(
              "parkour",
              "Parkour",
              "Traceur Urbain",
              "Valider 3 parcours de %s.",
              "/images/badge/escalade.png"),
          new BadgeDto(
              "randonnee",
              "Randonnee",
              "Grand Air",
              "Completer une sortie de %s de 10 km.",
              "/images/badge/cyclisme.png"),
          new BadgeDto(
              "plongee",
              "Plongee",
              "Respiration Calme",
              "Cumuler 60 minutes de %s.",
              "/images/badge/natation.png"),
          new BadgeDto(
              "cardio",
              "Course",
              "Cardio Tenace",
              "Terminer 5 circuits complets de %s.",
              "/images/badge/running_5km.png"),
          new BadgeDto(
              "tennis",
              "Tennis",
              "Echanges Longs",
              "Tenir 45 minutes de %s sans abandonner.",
              "/images/badge/running_42km.png"));

  private static final Map<String, List<String>> USER_BADGE_KEYS =
      Map.ofEntries(
          Map.entry("alice", List.of("rookie5k", "marathonHerbe")),
          Map.entry("benoit", List.of("rookie5k", "basketball")),
          Map.entry("owen", List.of("football", "rookie5k")),
          Map.entry("admin", List.of("cardio", "musculation", "yoga")),
          Map.entry("judy", List.of("parkour", "rookie5k")),
          Map.entry("nick", List.of("natation")),
          Map.entry("bellwether", List.of("yoga", "natation")),
          Map.entry("hiccup", List.of("rookie5k", "parkour")),
          Map.entry("astrid", List.of("escalade", "marathonien")),
          Map.entry("stoick", List.of("cyclisme")),
          Map.entry("fishlegs", List.of("escalade", "basketball")),
          Map.entry("rodney", List.of("parkour", "cyclisme")),
          Map.entry("cappy", List.of("yoga", "plongee")),
          Map.entry("fender", List.of("plongee", "parkour")),
          Map.entry("bigweld", List.of("randonnee", "escalade")),
          Map.entry("shifu", List.of("musculation", "yoga")),
          Map.entry("oogway", List.of("randonnee", "yoga")),
          Map.entry("po", List.of("cardio", "basketball")),
          Map.entry("taiLung", List.of("musculation", "escalade")),
          Map.entry("bogo", List.of("yoga")));

  private ReferenceBadgeCatalog() {}

  static BadgesDto seed(BadgeRepository badgeRepository, Map<String, Sport> sportsByName) {
    List<Badge> badges = BADGES.stream().map(dto -> createBadge(dto, sportsByName)).toList();
    List<Badge> savedBadges = badgeRepository.saveAll(badges);
    return new BadgesDto(savedBadges, Map.copyOf(indexByKey(savedBadges)));
  }

  static void assignToUsers(Map<String, User> usersByKey, Map<String, Badge> badgesByKey) {
    for (Map.Entry<String, List<String>> entry : USER_BADGE_KEYS.entrySet()) {
      User user = requireUser(usersByKey, entry.getKey());
      for (String badgeKey : entry.getValue()) {
        user.getBadges().add(requireBadge(badgesByKey, badgeKey));
      }
    }
  }

  private static Badge createBadge(BadgeDto dto, Map<String, Sport> sportsByName) {
    Sport sport = requireSport(sportsByName, dto.sportName());
    return new Badge(
        sport.getName() + " - " + dto.suffix(),
        String.format(dto.descriptionTemplate(), sport.getName()),
        dto.iconPath());
  }

  private static Map<String, Badge> indexByKey(List<Badge> badges) {
    java.util.LinkedHashMap<String, Badge> badgesByKey = new java.util.LinkedHashMap<>();
    for (int index = 0; index < BADGES.size(); index++) {
      badgesByKey.put(BADGES.get(index).key(), badges.get(index));
    }
    return badgesByKey;
  }

  private static Sport requireSport(Map<String, Sport> sportsByName, String sportName) {
    Sport sport = sportsByName.get(sportName);
    if (sport == null) {
      throw new IllegalStateException("Sport de demo introuvable: " + sportName);
    }
    return sport;
  }

  private static User requireUser(Map<String, User> usersByKey, String userKey) {
    User user = usersByKey.get(userKey);
    if (user == null) {
      throw new IllegalStateException("Utilisateur de demo introuvable: " + userKey);
    }
    return user;
  }

  private static Badge requireBadge(Map<String, Badge> badgesByKey, String badgeKey) {
    Badge badge = badgesByKey.get(badgeKey);
    if (badge == null) {
      throw new IllegalStateException("Badge de demo introuvable: " + badgeKey);
    }
    return badge;
  }

  record BadgesDto(List<Badge> allBadges, Map<String, Badge> badgesByKey) {}

  private record BadgeDto(
      String key, String sportName, String suffix, String descriptionTemplate, String iconPath) {}
}
