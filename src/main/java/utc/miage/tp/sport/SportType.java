package utc.miage.tp.sport;

import java.text.Normalizer;
import java.util.Locale;

public enum SportType {
  RUNNING("Course", true, true, false, false, false),
  CYCLING("Vélo", true, false, true, false, false),
  SWIMMING("Nage", true, true, false, false, false),
  TEAM("Collectif", true, false, true, false, false),
  STRENGTH("Renforcement", false, false, false, true, true),
  CLIMBING("Escalade", true, false, false, true, false),
  MOBILITY("Mobilité", false, false, false, false, true),
  AERIAL("Aérien", false, false, false, false, false),
  GENERIC("Séance", true, true, false, false, false);

  private final String label;
  private final boolean distanceRelevant;
  private final boolean paceRelevant;
  private final boolean speedRelevant;
  private final boolean strengthRelevant;
  private final boolean mobilityRelevant;

  SportType(
      String label,
      boolean distanceRelevant,
      boolean paceRelevant,
      boolean speedRelevant,
      boolean strengthRelevant,
      boolean mobilityRelevant) {
    this.label = label;
    this.distanceRelevant = distanceRelevant;
    this.paceRelevant = paceRelevant;
    this.speedRelevant = speedRelevant;
    this.strengthRelevant = strengthRelevant;
    this.mobilityRelevant = mobilityRelevant;
  }

  public String getLabel() {
    return label;
  }

  public boolean isDistanceRelevant() {
    return distanceRelevant;
  }

  public boolean isPaceRelevant() {
    return paceRelevant;
  }

  public boolean isSpeedRelevant() {
    return speedRelevant;
  }

  public boolean isStrengthRelevant() {
    return strengthRelevant;
  }

  public boolean isMobilityRelevant() {
    return mobilityRelevant;
  }

  public static SportType fromSportName(String sportName) {
    String name = normalize(sportName);

    if (name.contains("cyclisme") || name.contains("velo")) {
      return CYCLING;
    }
    if (name.contains("natation") || name.contains("nage") || name.contains("plongee")) {
      return SWIMMING;
    }
    if (name.contains("musculation")
        || name.contains("renforcement")
        || name.contains("circuit")
        || name.contains("cardio")) {
      return STRENGTH;
    }
    if (name.contains("escalade") || name.contains("bloc")) {
      return CLIMBING;
    }
    if (name.contains("yoga") || name.contains("mobilite")) {
      return MOBILITY;
    }
    if (name.contains("football") || name.contains("basketball") || name.contains("tennis")) {
      return TEAM;
    }
    if (name.contains("parachute")) {
      return AERIAL;
    }
    if (name.contains("course")
        || name.contains("sprint")
        || name.contains("fractionne")
        || name.contains("endurance")
        || name.contains("randonnee")
        || name.contains("parkour")
        || name.contains("agilite")) {
      return RUNNING;
    }

    return GENERIC;
  }

  private static String normalize(String value) {
    if (value == null) {
      return "";
    }
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase(Locale.ROOT);
  }
}
