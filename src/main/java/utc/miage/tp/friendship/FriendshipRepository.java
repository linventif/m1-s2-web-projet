package utc.miage.tp.friendship;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

  @Query(
      """
      select f
      from Friendship f
      where (f.requester.id = :userA and f.addressee.id = :userB)
         or (f.requester.id = :userB and f.addressee.id = :userA)
      """)
  Optional<Friendship> findRelationshipBetween(
      @Param("userA") Long userA, @Param("userB") Long userB);

  List<Friendship> findByAddresseeIdAndStatus(Long addresseeId, FriendshipStatus status);

  List<Friendship> findByRequesterIdAndStatus(Long requesterId, FriendshipStatus status);

  @Query(
      """
      select f
      from Friendship f
      where f.status = utc.miage.tp.friendship.FriendshipStatus.ACCEPTED
        and (f.requester.id = :userId or f.addressee.id = :userId)
      """)
  List<Friendship> findAcceptedForUser(@Param("userId") Long userId);
}
