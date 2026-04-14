package web.sportflow.friendship;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@Service
public class FriendshipService {

  private final FriendshipRepository friendshipRepository;
  private final UserRepository userRepository;

  public FriendshipService(
      FriendshipRepository friendshipRepository, UserRepository userRepository) {
    this.friendshipRepository = friendshipRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public Friendship sendRequest(Long requesterId, Long addresseeId) {
    if (requesterId.equals(addresseeId)) {
      throw new IllegalArgumentException("You cannot send a friend request to yourself.");
    }

    User requester = findUserOrThrow(requesterId);
    User addressee = findUserOrThrow(addresseeId);

    Friendship existing =
        friendshipRepository.findRelationshipBetween(requesterId, addresseeId).orElse(null);
    if (existing != null) {
      if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
        throw new IllegalArgumentException("You are already friends with this user.");
      }

      if (existing.getStatus() == FriendshipStatus.PENDING) {
        if (existing.getRequester().getId().equals(requesterId)) {
          throw new IllegalArgumentException("A request is already pending.");
        }

        // Opposite pending request already exists, so accept it.
        existing.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(existing);
      }

      existing.setRequester(requester);
      existing.setAddressee(addressee);
      existing.setStatus(FriendshipStatus.PENDING);
      return friendshipRepository.save(existing);
    }

    Friendship friendship = new Friendship(requester, addressee, FriendshipStatus.PENDING);
    return friendshipRepository.save(friendship);
  }

  @Transactional
  public Friendship acceptRequest(Long currentUserId, Long friendshipId) {
    Friendship friendship = findFriendshipOrThrow(friendshipId);
    if (!friendship.getAddressee().getId().equals(currentUserId)) {
      throw new IllegalArgumentException("Only the addressee can accept this request.");
    }
    if (friendship.getStatus() != FriendshipStatus.PENDING) {
      throw new IllegalArgumentException("Only pending requests can be accepted.");
    }
    friendship.setStatus(FriendshipStatus.ACCEPTED);
    return friendshipRepository.save(friendship);
  }

  @Transactional
  public Friendship refuseRequest(Long currentUserId, Long friendshipId) {
    Friendship friendship = findFriendshipOrThrow(friendshipId);
    if (!friendship.getAddressee().getId().equals(currentUserId)) {
      throw new IllegalArgumentException("Only the addressee can refuse this request.");
    }
    if (friendship.getStatus() != FriendshipStatus.PENDING) {
      throw new IllegalArgumentException("Only pending requests can be refused.");
    }
    friendship.setStatus(FriendshipStatus.REFUSED);
    return friendshipRepository.save(friendship);
  }

  @Transactional
  public void unfriend(Long currentUserId, Long otherUserId) {
    Friendship friendship =
        friendshipRepository
            .findRelationshipBetween(currentUserId, otherUserId)
            .orElseThrow(
                () -> new IllegalArgumentException("No friendship exists between these users."));

    if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
      throw new IllegalArgumentException("Only accepted friendships can be removed.");
    }

    friendshipRepository.delete(friendship);
  }

  @Transactional(readOnly = true)
  public List<Friendship> getIncomingPendingRequests(Long currentUserId) {
    return friendshipRepository.findByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING);
  }

  @Transactional(readOnly = true)
  public List<Friendship> getOutgoingPendingRequests(Long currentUserId) {
    return friendshipRepository.findByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING);
  }

  @Transactional(readOnly = true)
  public List<Friendship> getAcceptedFriendships(Long currentUserId) {
    return friendshipRepository.findAcceptedForUser(currentUserId);
  }

  @Transactional(readOnly = true)
  public Optional<Friendship> findRelationshipBetween(Long userAId, Long userBId) {
    if (userAId == null || userBId == null) {
      return Optional.empty();
    }
    return friendshipRepository.findRelationshipBetween(userAId, userBId);
  }

  @Transactional
  public Friendship createAcceptedFriendship(Long requesterId, Long addresseeId) {
    if (requesterId.equals(addresseeId)) {
      throw new IllegalArgumentException("Requester and addressee must be different users.");
    }

    User requester = findUserOrThrow(requesterId);
    User addressee = findUserOrThrow(addresseeId);

    Friendship friendship =
        friendshipRepository.findRelationshipBetween(requesterId, addresseeId).orElse(null);
    if (friendship == null) {
      friendship = new Friendship(requester, addressee, FriendshipStatus.ACCEPTED);
    } else {
      friendship.setRequester(requester);
      friendship.setAddressee(addressee);
      friendship.setStatus(FriendshipStatus.ACCEPTED);
    }
    return friendshipRepository.save(friendship);
  }

  private User findUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
  }

  private Friendship findFriendshipOrThrow(Long friendshipId) {
    return friendshipRepository
        .findById(friendshipId)
        .orElseThrow(() -> new IllegalArgumentException("Friendship not found: " + friendshipId));
  }
}
