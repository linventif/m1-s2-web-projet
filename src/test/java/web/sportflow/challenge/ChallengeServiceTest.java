package web.sportflow.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

  @Mock private ChallengeRepository challengeRepository;
  @Mock private FriendshipService friendshipService;
  @Mock private UserRepository userRepository;

  @InjectMocks private ChallengeService challengeService;

  private User user =
      new User(
          "Alice",
          "Marchand",
          "alice@example.com",
          60.0,
          165.0,
          Sex.FEMALE,
          LocalDate.of(2024, 3, 31),
          PracticeLevel.BEGINNER);

  @Test
  void createChallenge_createsAndPersistsCopiedChallenge() {
    Challenge input =
        new Challenge(
            "David Gogging's challenge",
            "It's basically a marahton",
            ChallengeType.REPETITION,
            40.0,
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 3, 31),
            user);
    when(challengeRepository.save(any(Challenge.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Challenge created = challengeService.createChallenge(input);

    assertNotSame(input, created);
    assertEquals("David Gogging's challenge", created.getTitle());
    assertEquals("It's basically a marahton", created.getDescription());
    assertEquals(ChallengeType.REPETITION, created.getType());
    assertEquals(40.0, created.getTargetValue());
    assertEquals(LocalDate.of(2024, 3, 1), created.getStartDate());
    assertEquals(LocalDate.of(2024, 3, 31), created.getEndDate());
    assertEquals(user, created.getCreator());
    verify(challengeRepository, times(2)).save(any(Challenge.class));
  }

  @Test
  void getAll_returnsRepositoryValues() {
    List<Challenge> expected =
        List.of(
            new Challenge(
                "David Gogging's challenge",
                "It's basically a marahton",
                ChallengeType.REPETITION,
                40.0,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 31),
                user),
            new Challenge(
                "Impossible push up challenge",
                "It's basically a push up",
                ChallengeType.ENDURENCE,
                1.0,
                LocalDate.of(2024, 3, 1),
                LocalDate.of(2024, 3, 31),
                user));
    when(challengeRepository.findAll()).thenReturn(expected);

    List<Challenge> result = challengeService.getAll();

    assertEquals(expected, result);
    verify(challengeRepository).findAll();
  }

  @Test
  void joinChallenge_addsCurrentUserAsParticipantWhenChallengeIsOpen() {
    user.setId(10L);
    Challenge challenge =
        new Challenge(
            "Avril actif",
            "Bouger tous les jours",
            ChallengeType.DUREE,
            30.0,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1),
            user);
    challenge.setId(1L);
    when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    challengeService.joinChallenge(1L, user);

    assertEquals(1, challenge.getParticipants().size());
    assertEquals(user, challenge.getParticipants().getFirst());
    verify(challengeRepository).save(challenge);
  }

  @Test
  void leaveChallenge_removesCurrentUserBeforeEndDate() {
    user.setId(10L);
    Challenge challenge =
        new Challenge(
            "Avril actif",
            "Bouger tous les jours",
            ChallengeType.DUREE,
            30.0,
            LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1),
            user);
    challenge.setId(1L);
    challenge.getParticipants().add(user);
    when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    challengeService.leaveChallenge(1L, user);

    assertEquals(0, challenge.getParticipants().size());
    verify(challengeRepository).save(challenge);
  }

  @Test
  void leaveChallenge_rejectsCancellationAfterEndDate() {
    user.setId(10L);
    Challenge challenge =
        new Challenge(
            "Challenge termine",
            "Trop tard",
            ChallengeType.DISTANCE,
            5.0,
            LocalDate.now().minusDays(10),
            LocalDate.now().minusDays(1),
            user);
    challenge.setId(1L);
    challenge.getParticipants().add(user);
    when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    assertThrows(IllegalArgumentException.class, () -> challengeService.leaveChallenge(1L, user));
  }
}
