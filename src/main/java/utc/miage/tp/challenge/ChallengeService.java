package utc.miage.tp.challenge;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChallengeService {

  private final ChallengeRepository challengeRepository;

  public ChallengeService(ChallengeRepository challengeRepository) {
    this.challengeRepository = challengeRepository;
  }

  @Transactional(readOnly = true)
  public List<Challenge> getAllStatuts() {
    return challengeRepository.findAll();
  }
}
