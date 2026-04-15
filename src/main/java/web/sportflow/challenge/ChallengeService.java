package web.sportflow.challenge;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.User;

@Service
public class ChallengeService {

  private final ChallengeRepository challengeRepository;
  private final FriendshipService friendshipService;

  public ChallengeService(
      ChallengeRepository challengeRepository, FriendshipService friendshipService) {
    this.challengeRepository = challengeRepository;
    this.friendshipService = friendshipService;
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
    newChallenge.setBadges(
        challenge.getBadges() == null ? new ArrayList<>() : new ArrayList<>(challenge.getBadges()));

    Challenge savedChallenge = challengeRepository.save(newChallenge);

    return challengeRepository.save(savedChallenge);
  }

  @Transactional(readOnly = true)
  public List<Challenge> getAll() {
    return challengeRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Challenge> getFriendsAndUserChallenge(User currentUser) {
    List<User> visibleUsers = friendshipService.getCurrentUserAndFriend(currentUser);
    if (visibleUsers.isEmpty()) {
      return List.of();
    }
    return challengeRepository.findByCreatorIn(visibleUsers);
  }
}
