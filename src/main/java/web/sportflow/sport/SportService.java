package web.sportflow.sport;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SportService {

  public static final String FIELD_DURATION = "duration";
  public static final String FIELD_DISTANCE = "distance";
  public static final String FIELD_REPETITIONS = "repetitions";
  public static final String FIELD_LOAD = "load";
  public static final String FIELD_CARDIO = "cardio";
  public static final String FIELD_ELEVATION = "elevation";
  public static final String FIELD_SPEED = "speed";
  public static final String FIELD_SCORE = "score";
  public static final String FIELD_ATTEMPTS = "attempts";
  public static final String FIELD_ACCURACY = "accuracy";
  public static final String FIELD_HEIGHT = "height";
  public static final String FIELD_DEPTH = "depth";
  public static final String FIELD_LAPS = "laps";
  public static final String FIELD_ROUNDS = "rounds";
  public static final String FIELD_MOBILITY = "mobility";

  private static final List<String> FIELD_KEYS =
      List.of(
          FIELD_DURATION,
          FIELD_DISTANCE,
          FIELD_REPETITIONS,
          FIELD_LOAD,
          FIELD_CARDIO,
          FIELD_ELEVATION,
          FIELD_SPEED,
          FIELD_SCORE,
          FIELD_ATTEMPTS,
          FIELD_ACCURACY,
          FIELD_HEIGHT,
          FIELD_DEPTH,
          FIELD_LAPS,
          FIELD_ROUNDS,
          FIELD_MOBILITY);

  private final SportRepository sportRepository;

  public SportService(SportRepository sportRepository) {
    this.sportRepository = sportRepository;
  }

  @Transactional
  public Sport createSport(Sport sport) {
    Sport newSport = new Sport(sport.getName(), sport.getMET());

    Sport savedSport = sportRepository.save(newSport);

    return sportRepository.save(savedSport);
  }

  @Transactional(readOnly = true)
  public List<Sport> getAll() {
    return sportRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Sport> findAll() {
    return sportRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<String> findAllNames() {
    return sportRepository.findAllNames();
  }

  @Transactional(readOnly = true)
  public Map<Long, Map<String, Boolean>> buildFieldProfiles(List<Sport> sports) {
    Map<Long, Map<String, Boolean>> profiles = new HashMap<>();
    if (sports == null) {
      return profiles;
    }

    for (Sport sport : sports) {
      if (sport != null && sport.getId() != null) {
        profiles.put(sport.getId(), buildFieldProfile(sport));
      }
    }
    return profiles;
  }

  public Map<String, Boolean> buildFieldProfile(Sport sport) {
    if (sport == null || sport.getName() == null || sport.getName().isBlank()) {
      return fieldProfile();
    }

    return switch (normalizeSportName(sport.getName())) {
      case "course", "marathon" ->
          fieldProfile(
              FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_SPEED, FIELD_LAPS, FIELD_SCORE);
      case "marche", "randonnee" ->
          fieldProfile(FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_SPEED);
      case "cyclisme" ->
          fieldProfile(FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_SPEED, FIELD_LAPS);
      case "natation" -> fieldProfile(FIELD_DISTANCE, FIELD_CARDIO, FIELD_SPEED, FIELD_LAPS);
      case "triathlon" ->
          fieldProfile(
              FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_SPEED, FIELD_LAPS, FIELD_SCORE);
      case "pentathlon" ->
          fieldProfile(
              FIELD_DISTANCE,
              FIELD_CARDIO,
              FIELD_SPEED,
              FIELD_SCORE,
              FIELD_ATTEMPTS,
              FIELD_ACCURACY);
      case "alpinisme" ->
          fieldProfile(FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_HEIGHT, FIELD_ATTEMPTS);
      case "escalade" ->
          fieldProfile(
              FIELD_REPETITIONS,
              FIELD_CARDIO,
              FIELD_SCORE,
              FIELD_ATTEMPTS,
              FIELD_HEIGHT,
              FIELD_MOBILITY);
      case "parkour" ->
          fieldProfile(
              FIELD_DISTANCE,
              FIELD_REPETITIONS,
              FIELD_CARDIO,
              FIELD_SCORE,
              FIELD_ATTEMPTS,
              FIELD_HEIGHT,
              FIELD_MOBILITY);
      case "musculation" ->
          fieldProfile(FIELD_REPETITIONS, FIELD_LOAD, FIELD_CARDIO, FIELD_SCORE, FIELD_ROUNDS);
      case "callisthenie" ->
          fieldProfile(FIELD_REPETITIONS, FIELD_CARDIO, FIELD_SCORE, FIELD_ROUNDS, FIELD_MOBILITY);
      case "crossfit" ->
          fieldProfile(
              FIELD_REPETITIONS, FIELD_LOAD, FIELD_CARDIO, FIELD_SCORE, FIELD_ROUNDS, FIELD_LAPS);
      case "yoga" -> fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_MOBILITY);
      case "gymnastique" ->
          fieldProfile(
              FIELD_REPETITIONS,
              FIELD_CARDIO,
              FIELD_SCORE,
              FIELD_ATTEMPTS,
              FIELD_HEIGHT,
              FIELD_MOBILITY);
      case "plongee" -> fieldProfile(FIELD_CARDIO, FIELD_DEPTH, FIELD_LAPS);
      case "speleologie" -> fieldProfile(FIELD_CARDIO, FIELD_ELEVATION, FIELD_DEPTH, FIELD_HEIGHT);
      case "saut_parachute", "base_jump" ->
          fieldProfile(FIELD_HEIGHT, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY);
      case "lance" -> fieldProfile(FIELD_DISTANCE, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY);
      case "saut" -> fieldProfile(FIELD_DISTANCE, FIELD_HEIGHT, FIELD_SCORE, FIELD_ATTEMPTS);
      case "football", "basketball", "hockey" ->
          fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY);
      case "tennis", "ping_pong", "squash" ->
          fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY, FIELD_ROUNDS);
      case "judo", "taekwondo", "karate", "boxe", "escrime", "lutte" ->
          fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY, FIELD_ROUNDS);
      case "ski", "patinage", "skate" ->
          fieldProfile(
              FIELD_DISTANCE, FIELD_CARDIO, FIELD_ELEVATION, FIELD_SPEED, FIELD_LAPS, FIELD_SCORE);
      case "curling", "tir_sportif", "tir_arc", "tir_cible" ->
          fieldProfile(FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY);
      case "luge", "bobsleigh", "formule_1", "motocyclisme" ->
          fieldProfile(FIELD_SPEED, FIELD_LAPS, FIELD_SCORE, FIELD_ATTEMPTS);
      case "aviron", "canoe_kayak" ->
          fieldProfile(FIELD_DISTANCE, FIELD_CARDIO, FIELD_SPEED, FIELD_LAPS);
      case "surf" -> fieldProfile(FIELD_CARDIO, FIELD_SPEED, FIELD_SCORE, FIELD_ATTEMPTS);
      case "voile" -> fieldProfile(FIELD_SPEED, FIELD_SCORE, FIELD_ATTEMPTS);
      case "equitation" ->
          fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_ATTEMPTS, FIELD_HEIGHT, FIELD_ACCURACY);
      case "repassage_extrem" ->
          fieldProfile(FIELD_SCORE, FIELD_ATTEMPTS, FIELD_ACCURACY, FIELD_HEIGHT);
      default -> fieldProfile(FIELD_CARDIO, FIELD_SCORE, FIELD_ATTEMPTS);
    };
  }

  private Map<String, Boolean> fieldProfile(String... enabledFields) {
    Map<String, Boolean> profile = new HashMap<>();
    FIELD_KEYS.forEach(field -> profile.put(field, false));
    profile.put(FIELD_DURATION, true);
    for (String field : enabledFields) {
      if (field != null) {
        profile.put(field, true);
      }
    }
    return profile;
  }

  private String normalizeSportName(String sportName) {
    return Normalizer.normalize(sportName, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .trim()
        .toLowerCase(Locale.ROOT)
        .replace('-', '_')
        .replace(' ', '_');
  }
}
