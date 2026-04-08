package utc.miage.tp.challenge;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

  @Mock private ChallengeRepository challengeRepository;

  @InjectMocks private ChallengeService challengeService;

  private User user =
      new User(
          "Alice",
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
}
