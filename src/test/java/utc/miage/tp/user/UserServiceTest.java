package utc.miage.tp.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void createUser_normalizesEmailAndEncodesPassword() {
    User input =
        new User(
            "Alice",
            "  ALICE@Example.COM  ",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(2024, 3, 31),
            PracticeLevel.BEGINNER);
    when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
    when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User created = userService.createUser(input, "secret", "unused", List.of(), List.of());

    assertEquals("alice@example.com", created.getEmail());
    assertEquals("encoded-secret", created.getPassword());
    verify(userRepository).existsByEmail("alice@example.com");
  }

  @Test
  void createUser_throwsWhenEmailAlreadyExists() {
    User input =
        new User(
            "Alice",
            " ALICE@example.com ",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(2024, 3, 31),
            PracticeLevel.BEGINNER);
    when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(input, "secret", "unused", List.of(), List.of()));

    assertEquals("Un utilisateur avec cet email existe deja.", exception.getMessage());
  }

  @Test
  void authenticate_returnsUserWhenPasswordMatches() {
    User stored =
        new User(
            "Alice",
            "alice@example.com",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(2024, 3, 31),
            PracticeLevel.BEGINNER);
    stored.setPassword("hashed-secret");
    when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(stored));
    when(passwordEncoder.matches("secret", "hashed-secret")).thenReturn(true);

    Optional<User> result = userService.authenticate(" ALICE@EXAMPLE.COM ", "secret");

    assertTrue(result.isPresent());
    assertEquals(stored, result.get());
  }
}
