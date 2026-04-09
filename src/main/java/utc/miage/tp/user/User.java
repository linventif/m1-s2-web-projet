package utc.miage.tp.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import utc.miage.tp.badge.Badge;
import utc.miage.tp.goal.Goal;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.workout.Workout;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String firstname;

  @Column(nullable = false)
  private String lastname;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "weight", nullable = false)
  private Double weight;

  @Column(nullable = false)
  private Double height;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Sex sex;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PracticeLevel level = PracticeLevel.BEGINNER;

  @ManyToMany
  @JoinTable(
      name = "user_sports",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "sport_id"))
  private List<Sport> sports = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Workout> workouts = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "user_goal",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "goal_id"))
  private List<Goal> goals = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "user_badge",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "badge_id"))
  private List<Badge> badges = new ArrayList<>();

  public User() {}

  public User(
      String firstname,
      String lastname,
      String email,
      String password,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.password = password;
    this.weight = weight;
    this.height = height;
    this.sex = sex;
    this.birthDate = birthDate;
    this.level = level;
  }

  public User(
      String firstname,
      String lastname,
      String email,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level) {
    this.firstname = firstname;
    this.lastname = lastname;
    this.email = email;
    this.weight = weight;
    this.height = height;
    this.sex = sex;
    this.birthDate = birthDate;
    this.level = level;
  }

  public User(
      String firstname, String lastname, String email, Double weight, Double height, Sex sex) {
    this(firstname, lastname, email, weight, height, sex, null, PracticeLevel.BEGINNER);
  }

  public Long getId() {
    return id;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public Double getWeight() {
    return weight;
  }

  public Double getHeight() {
    return height;
  }

  public Sex getSex() {
    return sex;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public PracticeLevel getLevel() {
    return level;
  }

  public List<Sport> getSports() {
    return sports;
  }

  public List<Workout> getWorkouts() {
    return workouts;
  }

  public List<Goal> getGoals() {
    return goals;
  }

  public List<Badge> getBadges() {
    return badges;
  }

  public Role getRole() {
    return role;
  }

  public String getProfileImagePath() {
    long imageIndex = id == null || id <= 0 ? 1 : ((id - 1) % 20) + 1;
    return "/images/profile/user_" + imageIndex + ".png";
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void setLevel(PracticeLevel level) {
    this.level = level;
  }

  public void setSports(List<Sport> sports) {
    this.sports = sports;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void addSport(Sport sport) {
    this.sports.add(sport);
  }

  public void removeSport(Sport sport) {
    this.sports.remove(sport);
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(this.role.getAuthority()));
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", role="
        + role
        + ", weight="
        + weight
        + ", height="
        + height
        + ", sex="
        + sex
        + ", firstname="
        + firstname
        + ", lastname="
        + lastname
        + ", email="
        + email
        + '}';
  }
}
