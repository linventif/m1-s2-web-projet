package web.sportflow.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceAdditionalTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  @Test
  void collectionAndLookupMethods_delegateToRepository() {
    User user = baseUser(1L);
    when(userRepository.findAll(any(org.springframework.data.domain.Sort.class)))
        .thenReturn(List.of(user));
    when(userRepository.findAll()).thenReturn(List.of(user));
    when(userRepository.findAll(PageRequest.of(0, 2))).thenReturn(new PageImpl<>(List.of(user)));
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    assertEquals(1, userService.getAll().size());
    Page<User> page = userService.getAll(PageRequest.of(0, 2));
    assertEquals(1, page.getTotalElements());
    assertTrue(userService.getUserById(1L).isPresent());
    assertEquals(1, userService.findAllUsers().size());
    assertEquals(user, userService.findById(1L));

    when(userRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> userService.findById(99L));
  }

  @Test
  void authAndUserDetailsBranches_coverMatchAndNotFound() {
    User user = baseUser(2L);
    user.setPassword("encoded");

    when(userRepository.findByEmail("alice@demo.local")).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("secret", "encoded")).thenReturn(false);

    assertTrue(userService.authenticate("alice@demo.local", "secret").isEmpty());

    when(userRepository.findByEmail("alice@demo.local")).thenReturn(Optional.of(user));
    assertEquals(user, userService.loadUserByUsername("alice@demo.local"));

    when(userRepository.findByEmail("missing@demo.local")).thenReturn(Optional.empty());
    assertThrows(
        UsernameNotFoundException.class,
        () -> userService.loadUserByUsername("missing@demo.local"));
  }

  @Test
  void registerAndSave_assignRolesAndEncodePassword() {
    RegistrationDTO dto =
        new RegistrationDTO("john@demo.local", "pwd", "John", "Doe", Sex.MALE, 70.0, 180.0);
    when(passwordEncoder.encode("pwd")).thenReturn("enc");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User basic = userService.registerUser(dto);
    assertEquals(Role.USER, basic.getRole());

    User admin = userService.registerUser(dto, true);
    assertEquals(Role.ADMIN, admin.getRole());

    userService.save(admin);
    verify(userRepository, atLeastOnce()).save(admin);
  }

  @Test
  void updateProfile_validationAndMetricsBranches_areCovered() {
    User current = baseUser(10L);
    when(userRepository.findByEmail("alice.new@demo.local")).thenReturn(Optional.empty());
    when(userRepository.save(current)).thenReturn(current);

    User updated =
        userService.updateCurrentUserProfile(
            current,
            " Alice ",
            " Martin ",
            "ALICE.NEW@DEMO.LOCAL",
            58.0,
            166.0,
            Sex.FEMALE,
            LocalDate.of(1996, 4, 2),
            PracticeLevel.ADVANCED);
    assertEquals("alice.new@demo.local", updated.getEmail());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                null,
                "a",
                "b",
                "x@x",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "",
                "b",
                "x@x",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "",
                "x@x",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "b",
                "",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "b",
                "x@x",
                0.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "b",
                "x@x",
                1.0,
                0.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current, "a", "b", "x@x", 1.0, 1.0, null, LocalDate.now(), PracticeLevel.BEGINNER));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current, "a", "b", "x@x", 1.0, 1.0, Sex.MALE, LocalDate.now(), null));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "b",
                "x@x",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now().plusDays(1),
                PracticeLevel.BEGINNER));

    User existing = baseUser(99L);
    when(userRepository.findByEmail("taken@demo.local")).thenReturn(Optional.of(existing));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            userService.updateCurrentUserProfile(
                current,
                "a",
                "b",
                "taken@demo.local",
                1.0,
                1.0,
                Sex.MALE,
                LocalDate.now(),
                PracticeLevel.BEGINNER));

    User noData = new User();
    assertEquals(0.0, userService.calculateBMI(noData));
    assertEquals(0.0, userService.calculateBMR(noData));

    assertTrue(userService.calculateBMI(current) > 0);
    assertTrue(userService.calculateBMR(current) > 0);
    assertFalse(userService.getWorkoutRecommendation(current).isBlank());
  }

  @Test
  void searchUsers_delegatesToRepository() {
    Page<User> page = new PageImpl<>(List.of(baseUser(1L)));
    when(userRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
            "al", "al", PageRequest.of(0, 5)))
        .thenReturn(page);

    Page<User> result = userService.searchUsers("al", PageRequest.of(0, 5));
    assertEquals(1, result.getTotalElements());
  }

  private User baseUser(Long id) {
    User user =
        new User(
            "Alice",
            "Martin",
            "alice@demo.local",
            "secret",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(1995, 1, 1),
            PracticeLevel.INTERMEDIATE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
