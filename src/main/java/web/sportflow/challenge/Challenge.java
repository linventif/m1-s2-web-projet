package web.sportflow.challenge;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import web.sportflow.badge.Badge;
import web.sportflow.user.User;

@Entity
@Table(name = "challenge")
public class Challenge {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(length = 500)
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChallengeType type;

  @Column(nullable = false)
  private Double targetValue;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "creator_id")
  private User creator;

  @ManyToMany
  @JoinTable(
      name = "challenge_badge",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "badge_id"))
  private List<Badge> badges = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "challenge_participant",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> participants = new ArrayList<>();

  public Challenge() {}

  public Challenge(
      String title,
      String description,
      ChallengeType type,
      Double targetValue,
      LocalDate startDate,
      LocalDate endDate,
      User creator) {
    this.title = title;
    this.description = description;
    this.type = type;
    this.targetValue = targetValue;
    this.startDate = startDate;
    this.endDate = endDate;
    this.creator = creator;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public ChallengeType getType() {
    return type;
  }

  public Double getTargetValue() {
    return targetValue;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public User getCreator() {
    return creator;
  }

  public List<Badge> getBadges() {
    return badges;
  }

  public List<User> getParticipants() {
    return participants;
  }

  public String getBadgeNames() {
    if (badges == null || badges.isEmpty()) {
      return "";
    }
    return badges.stream()
        .filter(Objects::nonNull)
        .map(Badge::getName)
        .filter(Objects::nonNull)
        .reduce((first, second) -> first + ", " + second)
        .orElse("");
  }

  public String getParticipantNames() {
    if (participants == null || participants.isEmpty()) {
      return "";
    }
    return participants.stream()
        .filter(Objects::nonNull)
        .map(user -> user.getFirstname() + " " + user.getLastname())
        .reduce((first, second) -> first + ", " + second)
        .orElse("");
  }

  public boolean isActive() {
    LocalDate today = LocalDate.now();
    return !today.isBefore(startDate) && !today.isAfter(endDate);
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setType(ChallengeType type) {
    this.type = type;
  }

  public void setTargetValue(Double targetValue) {
    this.targetValue = targetValue;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public void setBadges(List<Badge> badges) {
    this.badges = badges;
  }

  public void setParticipants(List<User> participants) {
    this.participants = participants;
  }
}
