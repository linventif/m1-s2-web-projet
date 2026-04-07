package utc.miage.tp.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import utc.miage.tp.sport.Sport;

@Entity
@Table(name = "users")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Column(name = "weight", nullable = false)
  private Double weight;

  @Column(name = "height", nullable = false)
  private Double height;

  @Column(name = "sex", nullable = false)
  private Sex sex;

  @ManyToOne
  @JoinColumn(name = "sports")
  private ArrayList<Sport> sports;

  @ManyToOne
  @JoinColumn(name = "friends")
  private ArrayList<User> friends;

  public Double getWeight() {
    return weight;
  }

  public ArrayList<Sport> getSports() {
    return this.sports;
  }

  public ArrayList<User> getFriends() {
    return this.friends;
  }

  public void addFriends(User user) {
    this.friends.add(user);
  }

  public void removeFriends(User user) {
    this.friends.remove(user);
  }

  public void addSports(Sport sport) {
    this.sports.add(sport);
  }

  public void removeSports(Sport sport) {
    this.sports.remove(sport);
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public Double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  public User() {}

  public User(String name, String email, Double weight, Double height, Sex sex) {
    this.name = name;
    this.email = email;
    this.weight = weight;
    this.height = height;
    this.sex = sex;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
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
}
