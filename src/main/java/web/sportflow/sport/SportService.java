package web.sportflow.sport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SportService {

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
        profiles.put(sport.getId(), buildDefaultFieldProfile());
      }
    }
    return profiles;
  }

  private Map<String, Boolean> buildDefaultFieldProfile() {
    return Map.of(
        "distance", true,
        "strength", true,
        "mobility", true);
  }
}
