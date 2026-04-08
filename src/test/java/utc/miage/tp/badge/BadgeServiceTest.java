package utc.miage.tp.badge;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BadgeServiceTest {

  @Mock private BadgeRepository badgeRepository;

  @InjectMocks private BadgeService badgeService;

  @Test
  void getAll_returnsRepositoryValues() {
    List<Badge> expected =
        List.of(
            new Badge(
                "David Gogging's badge", "David Gogging's badge from David Gogging's challenge"),
            new Badge(
                "Impossible push up badge",
                "Impossible push up badge badge from Impossible push up badge challenge"));
    when(badgeRepository.findAll()).thenReturn(expected);

    List<Badge> result = badgeService.getAll();

    assertEquals(expected, result);
    verify(badgeRepository).findAll();
  }
}
