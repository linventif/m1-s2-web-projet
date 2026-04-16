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
              List.of("stoick", "fender", "cappy", "owen")),
          new ChallengeDto(
              "Course",
              "Defi Rookie 5K",
              "Atteindre 5 km cumules.",
              ChallengeType.DISTANCE,
              5.0,
              null,
              null,
              true,
              List.of("rookie5k"),
              List.of()),
          new ChallengeDto(
              "Course",
              "Defi Semi Marathon",
              "Atteindre 21 km cumules.",
              ChallengeType.DISTANCE,
              21.0,
              null,
              null,
              true,
              List.of("marathonHerbe"),
              List.of()),
          new ChallengeDto(
              "Course",
              "Defi Marathon 42K",
              "Atteindre 42 km cumules.",
              ChallengeType.DISTANCE,
              42.0,
              null,
              null,
              true,
              List.of("marathonien"),
              List.of()),
          new ChallengeDto(
              "Natation",
              "Defi Natation Endurance",
              "Cumuler 180 minutes.",
              ChallengeType.DUREE,
              180.0,
              null,
              null,
              true,
              List.of("natation"),
              List.of()),
          new ChallengeDto(
              "Cyclisme",
              "Defi Cyclisme 80K",
              "Atteindre 80 km cumules.",
              ChallengeType.DISTANCE,
              80.0,
              null,
              null,
              true,
              List.of("cyclisme"),
              List.of()),
          new ChallengeDto(
              "Escalade",
              "Defi Escalade Focus",
              "Cumuler 1500 calories.",
              ChallengeType.CALORIE,
              1500.0,
              null,
              null,
              true,
              List.of("escalade"),
              List.of()),
          new ChallengeDto(
              "Yoga",
              "Defi Yoga Flow",
              "Cumuler 120 minutes.",
              ChallengeType.DUREE,
              120.0,
              null,
              null,
              true,
              List.of("yoga"),
              List.of()),
          new ChallengeDto(
              "Course",
              "Run Flash 7 jours",
              "Atteindre 15 km avant la fin de la semaine.",
              ChallengeType.DISTANCE,
              15.0,
              -1L,
              6L,
              true,
              List.of(),
              List.of()),
          new ChallengeDto(
              "CrossFit",
              "Cardio Week",
              "Cumuler 500 kcal pendant cette semaine.",
              ChallengeType.CALORIE,
              500.0,
              0L,
              7L,
              true,
              List.of(),
              List.of()),
          new ChallengeDto(
              "Natation",
              "Natation Express",
              "Cumuler 60 minutes avant la date de fin.",
              ChallengeType.DUREE,
              60.0,
              -2L,
              5L,
              true,
              List.of(),
              List.of()),
          new ChallengeDto(
              "Course",
              "Run entre amis",
              "Cumuler 20 km pendant la periode.",
              ChallengeType.DISTANCE,
              20.0,
              -1,
              10,
              List.of(),
              List.of("judy", "nick", "alice")),
          new ChallengeDto(
              "CrossFit",
              "Cardio de quartier",
              "Cumuler 120 minutes pendant la periode.",
              ChallengeType.DUREE,
              120.0,
              -4,
              12,
              List.of(),
              List.of("rodney", "benoit", "hiccup")),
          new ChallengeDto(
              "Yoga",
              "Mobilite crew",
              "Valider 3 seances pendant la periode.",
              ChallengeType.REPETITION,
              3.0,
              -2,
              14,
              List.of(),
              List.of("cappy", "astrid", "fishlegs")));

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
              dateFromOffset(today, dto.startOffsetDays()),
              dateFromOffset(today, dto.endOffsetDays()),
              creator,
              dto.official());
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

  private static LocalDate dateFromOffset(LocalDate today, Long offsetDays) {
    return offsetDays == null ? null : today.plusDays(offsetDays);
  }

  private record ChallengeDto(
      String sportName,
      String title,
      String descriptionTemplate,
      ChallengeType type,
      Double targetValue,
      Long startOffsetDays,
      Long endOffsetDays,
      boolean official,
      List<String> badgeKeys,
      List<String> participantKeys) {

    private ChallengeDto(
        String sportName,
        String title,
        String descriptionTemplate,
        ChallengeType type,
        Double targetValue,
        long startOffsetDays,
        long endOffsetDays,
        List<String> badgeKeys,
        List<String> participantKeys) {
      this(
          sportName,
          title,
          descriptionTemplate,
          type,
          targetValue,
          startOffsetDays,
          endOffsetDays,
          false,
          badgeKeys,
          participantKeys);
    }
  }
}
