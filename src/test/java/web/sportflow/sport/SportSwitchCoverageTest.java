package web.sportflow.sport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SportSwitchCoverageTest {

  @Test
  void allSports_coverRelevanceSwitches() {
    for (SportName name : SportName.values()) {
      Sport sport = new Sport(name, 8.0);
      assertNotNull(sport.getDisplayName());
      sport.isDistanceRelevant();
      sport.isStrengthRelevant();
      sport.isMobilityRelevant();
    }

    Sport unnamed = new Sport();
    assertEquals("Seance", unnamed.getDisplayName());
    unnamed.isDistanceRelevant();
    unnamed.isStrengthRelevant();
    unnamed.isMobilityRelevant();
  }
}
