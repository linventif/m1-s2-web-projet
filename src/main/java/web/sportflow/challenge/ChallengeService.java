package web.sportflow.challenge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@Service
public class ChallengeService {

  private final ChallengeRepository challengeRepository;
  private final FriendshipService friendshipService;
  private final UserRepository userRepository;

  public ChallengeService(
      ChallengeRepository challengeRepository,
      FriendshipService friendshipService,
      UserRepository userRepository) {
    this.challengeRepository = challengeRepository;
    this.friendshipService = friendshipService;
    this.userRepository = userRepository;
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
  public List<Challenge> searchChallenges(String query) {
    String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    return challengeRepository.findAll().stream()
        .filter(challenge -> normalizedQuery.isBlank() || matchesQuery(challenge, normalizedQuery))
        .sorted(
            Comparator.comparing(
                    Challenge::getEndDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(
                    Challenge::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
        .toList();
  }

  @Transactional
  public void joinChallenge(Long challengeId, User currentUser) {
    Challenge challenge = requireChallenge(challengeId);
    User participant = requireUser(currentUser);
    ensureChallengeIsOpen(challenge, "La date de fin du challenge est depassee.");

    boolean alreadyParticipating =
        challenge.getParticipants().stream()
            .anyMatch(user -> user != null && participant.getId().equals(user.getId()));
    if (!alreadyParticipating) {
      challenge.getParticipants().add(participant);
      challengeRepository.save(challenge);
    }
  }

  @Transactional
  public void leaveChallenge(Long challengeId, User currentUser) {
    Challenge challenge = requireChallenge(challengeId);
    User participant = requireUser(currentUser);
    ensureChallengeIsOpen(
        challenge, "La participation ne peut plus etre annulee apres la date de fin.");

    boolean removed =
        challenge
            .getParticipants()
            .removeIf(user -> user != null && participant.getId().equals(user.getId()));
    if (!removed) {
      throw new IllegalArgumentException("Vous ne participez pas a ce challenge.");
    }
    challengeRepository.save(challenge);
  }

  @Transactional(readOnly = true)
  public List<Challenge> getFriendsAndUserChallenge(User currentUser) {
    List<User> visibleUsers = friendshipService.getCurrentUserAndFriend(currentUser);
    if (visibleUsers.isEmpty()) {
      return List.of();
    }
    return challengeRepository.findByCreatorIn(visibleUsers);
  }

  private boolean matchesQuery(Challenge challenge, String normalizedQuery) {
    return contains(challenge.getTitle(), normalizedQuery)
        || contains(challenge.getDescription(), normalizedQuery)
        || (challenge.getType() != null
            && (contains(challenge.getType().name(), normalizedQuery)
                || contains(challenge.getType().getLabelFr(), normalizedQuery)));
  }

  private boolean contains(String value, String normalizedQuery) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
  }

  private Challenge requireChallenge(Long challengeId) {
    return challengeRepository
        .findById(challengeId)
        .orElseThrow(() -> new IllegalArgumentException("Challenge introuvable."));
  }

  private User requireUser(User currentUser) {
    if (currentUser == null || currentUser.getId() == null) {
      throw new IllegalArgumentException("Utilisateur connecte introuvable.");
    }
    return userRepository
        .findById(currentUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("Utilisateur connecte introuvable."));
  }

  private void ensureChallengeIsOpen(Challenge challenge, String message) {
    if (challenge.getEndDate() == null || LocalDate.now().isAfter(challenge.getEndDate())) {
      throw new IllegalArgumentException(message);
    }
  }
}
