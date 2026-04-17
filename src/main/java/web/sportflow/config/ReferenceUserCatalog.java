package web.sportflow.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;

final class ReferenceUserCatalog {

  // Intentional default credential used only for local/demo reference data seeding.
  @SuppressWarnings("java:S2068")
  private static final String DEMO_PASSWORD = "demo123";

  private static final List<UserDto> USERS =
      List.of(
          new UserDto(
              "alice",
              "Alice",
              "Martin",
              "alice.martin@demo.local",
              65.5,
              165.0,
              Sex.FEMALE,
              LocalDate.of(1998, 4, 14),
              PracticeLevel.BEGINNER,
              Role.USER,
              "alice_martin.png"),
          new UserDto(
              "benoit",
              "Benoit",
              "Leroy",
              "benoit.leroy@demo.local",
              75.5,
              180.0,
              Sex.MALE,
              LocalDate.of(1994, 8, 23),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "benoit_leroy.png"),
          new UserDto(
              "owen",
              "Owen",
              "Mercier",
              "owen.mercier@demo.local",
              85.0,
              185.0,
              Sex.MALE,
              LocalDate.of(1990, 1, 5),
              PracticeLevel.ADVANCED,
              Role.USER,
              "owen_mercier.png"),
          new UserDto(
              "admin",
              "Admin",
              "BG",
              "admin@demo.local",
              70.0,
              175.0,
              Sex.FEMALE,
              LocalDate.of(1992, 6, 2),
              PracticeLevel.INTERMEDIATE,
              Role.ADMIN,
              "admin_bg.png"),
          new UserDto(
              "judy",
              "Judy",
              "Hopps",
              "judy.hopps@demo.local",
              38.0,
              102.0,
              Sex.FEMALE,
              LocalDate.of(1999, 3, 12),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "judy_hopps.png"),
          new UserDto(
              "nick",
              "Nick",
              "Wilde",
              "nick.wilde@demo.local",
              72.0,
              168.0,
              Sex.MALE,
              LocalDate.of(1995, 7, 27),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "nick_wilde.png"),
          new UserDto(
              "bogo",
              "Chief",
              "Bogo",
              "chief.bogo@demo.local",
              110.0,
              190.0,
              Sex.MALE,
              LocalDate.of(1986, 11, 8),
              PracticeLevel.ADVANCED,
              Role.USER,
              "chief_bogo.png"),
          new UserDto(
              "bellwether",
              "Dawn",
              "Bellwether",
              "dawn.bellwether@demo.local",
              55.0,
              150.0,
              Sex.FEMALE,
              LocalDate.of(1997, 9, 30),
              PracticeLevel.BEGINNER,
              Role.USER,
              "dawn_bellwether.png"),
          new UserDto(
              "hiccup",
              "Hiccup",
              "Haddock",
              "hiccup.haddock@demo.local",
              68.0,
              178.0,
              Sex.MALE,
              LocalDate.of(1998, 5, 20),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "hiccup_haddock.png"),
          new UserDto(
              "astrid",
              "Astrid",
              "Hofferson",
              "astrid.hofferson@demo.local",
              61.0,
              170.0,
              Sex.FEMALE,
              LocalDate.of(1998, 2, 16),
              PracticeLevel.ADVANCED,
              Role.USER,
              "astrid_hofferson.png"),
          new UserDto(
              "stoick",
              "Stoick",
              "the Vast",
              "stoick.vast@demo.local",
              120.0,
              198.0,
              Sex.MALE,
              LocalDate.of(1980, 10, 10),
              PracticeLevel.ADVANCED,
              Role.USER,
              "stoick_the_vast.png"),
          new UserDto(
              "fishlegs",
              "Fishlegs",
              "Ingerman",
              "fishlegs.ingerman@demo.local",
              95.0,
              182.0,
              Sex.MALE,
              LocalDate.of(1997, 12, 22),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "fishlegs_ingerman.png"),
          new UserDto(
              "rodney",
              "Rodney",
              "Copperbottom",
              "rodney.copperbottom@demo.local",
              78.0,
              176.0,
              Sex.MALE,
              LocalDate.of(1996, 4, 4),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "rodney_copperbottom.png"),
          new UserDto(
              "cappy",
              "Cappy",
              "Barra",
              "cappy@demo.local",
              58.0,
              168.0,
              Sex.FEMALE,
              LocalDate.of(1996, 3, 3),
              PracticeLevel.BEGINNER,
              Role.USER,
              "cappy_barra.png"),
          new UserDto(
              "fender",
              "Fender",
              "Def",
              "fender@demo.local",
              72.0,
              174.0,
              Sex.MALE,
              LocalDate.of(1995, 1, 15),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "fender_def.png"),
          new UserDto(
              "bigweld",
              "Bigweld",
              "Bold",
              "bigweld@demo.local",
              105.0,
              192.0,
              Sex.MALE,
              LocalDate.of(1984, 7, 9),
              PracticeLevel.ADVANCED,
              Role.USER,
              "bigweld_bold.png"),
          new UserDto(
              "shifu",
              "Maitre Shifu",
              "Me",
              "shifu@demo.local",
              56.0,
              152.0,
              Sex.MALE,
              LocalDate.of(1982, 1, 12),
              PracticeLevel.ADVANCED,
              Role.USER,
              "maitre_shifu_me.png"),
          new UserDto(
              "oogway",
              "Maitre Oogway",
              "Away",
              "oogway@demo.local",
              73.0,
              168.0,
              Sex.MALE,
              LocalDate.of(1970, 5, 9),
              PracticeLevel.ADVANCED,
              Role.USER,
              "maitre_oogway_away.png"),
          new UserDto(
              "po",
              "Po Ping",
              "Pong",
              "po.ping@demo.local",
              120.0,
              182.0,
              Sex.MALE,
              LocalDate.of(1996, 11, 4),
              PracticeLevel.INTERMEDIATE,
              Role.USER,
              "po_ping_pong.png"),
          new UserDto(
              "taiLung",
              "Tai Lung",
              "Shi",
              "tai.lung@demo.local",
              98.0,
              188.0,
              Sex.MALE,
              LocalDate.of(1991, 2, 17),
              PracticeLevel.ADVANCED,
              Role.USER,
              "tai_lung_shi.png"));

  private ReferenceUserCatalog() {}

  static UsersDto seed(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    Map<String, User> usersByKey = new LinkedHashMap<>();

    for (UserDto dto : USERS) {
      User user = createUser(dto, passwordEncoder);
      usersByKey.put(dto.key(), user);
    }

    userRepository.saveAll(new ArrayList<>(usersByKey.values()));

    Map<User, String> avatarByUser = new LinkedHashMap<>();
    for (UserDto dto : USERS) {
      avatarByUser.put(requireUser(usersByKey, dto.key()), dto.avatarFileName());
    }

    return new UsersDto(Map.copyOf(usersByKey), Map.copyOf(avatarByUser));
  }

  @SuppressWarnings("java:S6437") // Demo credential; not used outside local sample data.
  private static User createUser(UserDto dto, PasswordEncoder passwordEncoder) {
    User user =
        new User(
            dto.firstname(),
            dto.lastname(),
            dto.email(),
            dto.weight(),
            dto.height(),
            dto.sex(),
            dto.birthDate(),
            dto.level());
    user.setProfileImagePath(null);
    user.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
    user.setRole(dto.role());
    return user;
  }

  record UsersDto(Map<String, User> usersByKey, Map<User, String> avatarByUser) {
    User user(String key) {
      return requireUser(usersByKey, key);
    }
  }

  private static User requireUser(Map<String, User> usersByKey, String key) {
    User user = usersByKey.get(key);
    if (user == null) {
      throw new IllegalStateException("Utilisateur de demo introuvable: " + key);
    }
    return user;
  }

  private record UserDto(
      String key,
      String firstname,
      String lastname,
      String email,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level,
      Role role,
      String avatarFileName) {}
}
