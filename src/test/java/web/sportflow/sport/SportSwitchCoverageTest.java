package web.sportflow.sport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

  @Test
  void fieldProfiles_coverNormalizationGroupsAndCollectionBuilder() {
    SportService sportService = new SportService(null);

    Map<String, Boolean> defaultProfile = sportService.buildFieldProfile(new Sport("   ", 8.0));
    assertTrue(defaultProfile.get(SportService.FIELD_DURATION));
    assertFalse(defaultProfile.get(SportService.FIELD_DISTANCE));

    Map<String, Boolean> equitation = sportService.buildFieldProfile(new Sport("Équitation", 6.0));
    assertTrue(equitation.get(SportService.FIELD_HEIGHT));
    assertTrue(equitation.get(SportService.FIELD_ACCURACY));

    Map<String, Boolean> baseJump = sportService.buildFieldProfile(new Sport("Base-Jump", 4.0));
    assertTrue(baseJump.get(SportService.FIELD_HEIGHT));
    assertTrue(baseJump.get(SportService.FIELD_ATTEMPTS));

    Map<String, Boolean> tirArc = sportService.buildFieldProfile(new Sport("Tir Arc", 3.0));
    assertTrue(tirArc.get(SportService.FIELD_SCORE));
    assertTrue(tirArc.get(SportService.FIELD_ACCURACY));

    Map<String, Boolean> unknown = sportService.buildFieldProfile(new Sport("Sport inconnu", 5.0));
    assertTrue(unknown.get(SportService.FIELD_CARDIO));
    assertTrue(unknown.get(SportService.FIELD_SCORE));
    assertTrue(unknown.get(SportService.FIELD_ATTEMPTS));
    assertFalse(unknown.get(SportService.FIELD_DISTANCE));

    Sport kept = new Sport("Course", 9.0);
    kept.setId(10L);
    Sport noId = new Sport("Cyclisme", 8.0);
    List<Sport> mixedSports = new ArrayList<>();
    mixedSports.add(null);
    mixedSports.add(noId);
    mixedSports.add(kept);
    Map<Long, Map<String, Boolean>> profiles = sportService.buildFieldProfiles(mixedSports);
    assertEquals(1, profiles.size());
    assertTrue(profiles.containsKey(10L));
    assertTrue(sportService.buildFieldProfiles(null).isEmpty());
  }
}
