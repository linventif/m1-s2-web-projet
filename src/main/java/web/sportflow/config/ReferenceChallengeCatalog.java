package web.sportflow.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import web.sportflow.badge.Badge;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeRepository;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.sport.Sport;
import web.sportflow.user.User;

final class ReferenceChallengeCatalog {

  private static final List<ChallengeDto> CHALLENGES =
      List.of(
          new ChallengeDto(
              "Course",
              "Challenge Running 25K",
              "Cumuler 25 km en %s sur la periode.",
              ChallengeType.DISTANCE,
              25.0,
              -5,
              21,
              List.of("rookie5k", "marathonHerbe"),
              List.of("alice", "judy", "nick", "hiccup", "fishlegs", "owen")),
          new ChallengeDto(
              "Natation",
              "Challenge Natation Endurance",
              "Cumuler 180 minutes de %s.",
              ChallengeType.DUREE,
              180.0,
              -3,
              18,
              List.of("natation"),
              List.of("nick", "cappy", "bellwether", "stoick", "fender")),
          new ChallengeDto(
              "Cyclisme",
              "Challenge Cyclisme 80K",
              "Atteindre 80 km en %s.",
              ChallengeType.DISTANCE,
              80.0,
              -7,
              28,
              List.of("cyclisme"),
              List.of("stoick", "rodney", "bigweld", "benoit")),
          new ChallengeDto(
              "Escalade",
              "Challenge Escalade Vitesse",
              "Cumuler 1500 calories sur des seances de %s.",
              ChallengeType.CALORIE,
              1500.0,
              -2,
              20,
              List.of("escalade", "marathonien"),
              List.of("astrid", "bogo", "fishlegs", "bigweld", "taiLung")),
          new ChallengeDto(
              "Musculation",
              "Challenge Musculation Regulier",
              "Enregistrer 12 seances de %s.",
              ChallengeType.REPETITION,
              12.0,
              -1,
              30,
              List.of("musculation", "cardio"),
              List.of("bogo", "stoick", "shifu", "po", "taiLung", "admin")),
          new ChallengeDto(
              "Basketball",
              "Challenge Basket Equipe",
              "Cumuler 240 minutes de %s en collectif.",
              ChallengeType.DUREE,
              240.0,
              -4,
              17,
              List.of("basketball", "cardio"),
              List.of("fishlegs", "alice", "benoit", "po", "nick")),
          new ChallengeDto(
              "Parkour",
              "Challenge Traceurs Urbains",
              "Valider 6 parcours techniques de %s.",
              ChallengeType.REPETITION,
              6.0,
              -6,
              24,
              List.of("parkour", "escalade"),
              List.of("rodney", "fender", "judy", "hiccup")),
          new ChallengeDto(
              "Randonnee",
              "Challenge Grand Air",
              "Cumuler 35 km de %s avant la fin du mois.",
              ChallengeType.DISTANCE,
              35.0,
              -8,
              22,
              List.of("randonnee", "cyclisme"),
              List.of("bigweld", "oogway", "shifu", "bellwether")),
          new ChallengeDto(
              "Football",
              "Challenge Pressing",
              "Accumuler 18 km d'efforts en %s.",
              ChallengeType.DISTANCE,
              18.0,
              -3,
              19,
              List.of("football", "rookie5k"),
              List.of("astrid", "owen", "benoit", "hiccup")),
          new ChallengeDto(
              "Plongee",
              "Challenge Respiration Calme",
              "Cumuler 120 minutes de %s ou nage encadree.",
              ChallengeType.DUREE,
              120.0,
              -2,
              26,
              List.of("plongee", "natation"),
              List.of("stoick", "fender", "cappy", "owen")));

  private ReferenceChallengeCatalog() {}

  static List<Challenge> seed(
      ChallengeRepository challengeRepository,
      LocalDate today,
      User creator,
      Map<String, Sport> sportsByName,
      Map<String, User> usersByKey,
      Map<String, Badge> badgesByKey) {
    List<Challenge> challenges = new ArrayList<>();

    for (ChallengeDto dto : CHALLENGES) {
      Sport sport = requireSport(sportsByName, dto.sportName());
      Challenge challenge =
          new Challenge(
              dto.title() + " (" + sport.getName() + ")",
              String.format(dto.descriptionTemplate(), sport.getName()),
              dto.type(),
              dto.targetValue(),
              today.plusDays(dto.startOffsetDays()),
              today.plusDays(dto.endOffsetDays()),
              creator);
      addBadges(challenge, badgesByKey, dto.badgeKeys());
      addParticipants(challenge, usersByKey, dto.participantKeys());
      challenges.add(challenge);
    }

    return challengeRepository.saveAll(challenges);
  }

  private static void addBadges(
      Challenge challenge, Map<String, Badge> badgesByKey, List<String> badgeKeys) {
    for (String badgeKey : badgeKeys) {
      challenge.getBadges().add(requireBadge(badgesByKey, badgeKey));
    }
  }

  private static void addParticipants(
      Challenge challenge, Map<String, User> usersByKey, List<String> participantKeys) {
    for (String participantKey : participantKeys) {
      User participant = requireUser(usersByKey, participantKey);
      if (!challenge.getParticipants().contains(participant)) {
        challenge.getParticipants().add(participant);
      }
    }
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

  private record ChallengeDto(
      String sportName,
      String title,
      String descriptionTemplate,
      ChallengeType type,
      Double targetValue,
      long startOffsetDays,
      long endOffsetDays,
      List<String> badgeKeys,
      List<String> participantKeys) {}
}
