package web.sportflow.sport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SportServiceTest {

  @Mock private SportRepository sportRepository;

  @InjectMocks private SportService sportService;

  @Test
  void createSport_createsAndPersistsCopiedSport() {
    Sport input = new Sport("Swim", 9.5);
    when(sportRepository.save(any(Sport.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Sport created = sportService.createSport(input);

    assertNotSame(input, created);
    assertEquals("Swim", created.getName());
    assertEquals(9.5, created.getCaloryPerMinutes());
    verify(sportRepository, times(2)).save(any(Sport.class));
  }

  @Test
  void getAllStatuts_returnsRepositoryValues() {
    List<Sport> expected = List.of(new Sport("Bike", 7.0), new Sport("Run", 10.0));
    when(sportRepository.findAll()).thenReturn(expected);

    List<Sport> result = sportService.getAllStatuts();

    assertEquals(expected, result);
    verify(sportRepository).findAll();
  }
}
