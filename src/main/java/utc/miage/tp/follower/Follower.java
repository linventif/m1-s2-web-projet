package utc.miage.tp.follower;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import utc.miage.tp.user.User;

@Entity
@Table(name = "followers")
public class Follower {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "follower", nullable = false)
  private User userFollower;

  @ManyToOne
  @JoinColumn(name = "followed", nullable = false)
  private User userFollowed;

  public User getUserFollower() {
    return userFollower;
  }

  public void setUserFollower(User userFollower) {
    this.userFollower = userFollower;
  }

  public User getUserFollowed() {
    return userFollowed;
  }

  public void setUserFollowed(User userFollowed) {
    this.userFollowed = userFollowed;
  }

  public Follower() {}

  public Follower(User userFollower, User userFollowed) {
    this.userFollower = userFollower;
    this.userFollowed = userFollowed;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
