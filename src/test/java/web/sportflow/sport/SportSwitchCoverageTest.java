package web.sportflow.sport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SportSwitchCoverageTest {

  @Test
  void sportServiceFieldProfiles_coverKnownSports() {
    SportService sportService = new SportService(null);
    for (String name :
        List.of("Course", "Musculation", "Yoga", "Natation", "Escalade", "Base_Jump")) {
      Sport sport = new Sport(name, 8.0);
      Map<String, Boolean> profile = sportService.buildFieldProfile(sport);
      assertNotNull(sport.getDisplayName());
      assertTrue(profile.get(SportService.FIELD_DURATION));
    }

    Sport unnamed = new Sport();
    assertEquals("Course", unnamed.getDisplayName());
    assertTrue(sportService.buildFieldProfile(unnamed).get(SportService.FIELD_DURATION));
  }
}
