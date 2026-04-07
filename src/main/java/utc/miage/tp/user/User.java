package utc.miage.tp.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import utc.miage.tp.sport.Sport;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

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
}
