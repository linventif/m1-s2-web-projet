package utc.miage.tp.sport;

import java.util.List;
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
    Sport newSport = new Sport(sport.getName(), sport.getCaloryPerMinutes());

    Sport savedSport = sportRepository.save(newSport);

    return sportRepository.save(savedSport);
  }

  @Transactional(readOnly = true)
  public List<Sport> getAllStatuts() {
    return sportRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Sport> findAll() {
    return sportRepository.findAll();
  }
}
