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

  @Transactional
  public Challenge createChallenge(Challenge challenge) {
    Challenge newChallenge =
        new Challenge(
            challenge.getTitle(),
            challenge.getDescription(),
            challenge.getType(),
            challenge.getTargetValue(),
            challenge.getStartDate(),
            challenge.getEndDate(),
            challenge.getCreator());

    Challenge savedChallenge = challengeRepository.save(newChallenge);

    return challengeRepository.save(savedChallenge);
  }

  @Transactional(readOnly = true)
  public List<Challenge> getAll() {
    return challengeRepository.findAll();
  }
}
