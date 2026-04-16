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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import web.sportflow.badge.Badge;
import web.sportflow.sport.Sport;
import web.sportflow.user.User;

@Entity
@Table(name = "challenge")
public class Challenge {
  private static final DateTimeFormatter SHORT_US_DATE = DateTimeFormatter.ofPattern("MM/dd");

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

  @Column(nullable = true)
  private LocalDate startDate;

  @Column(nullable = true)
  private LocalDate endDate;

  @ManyToOne(optional = false)
  @JoinColumn(name = "creator_id")
  private User creator;

  @Column(nullable = false)
  private boolean official = false;

  @ManyToMany
  @JoinTable(
      name = "challenge_sport",
      joinColumns = @JoinColumn(name = "challenge_id"),
      inverseJoinColumns = @JoinColumn(name = "sport_id"))
  private List<Sport> sports = new ArrayList<>();

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
    this(title, description, type, targetValue, startDate, endDate, creator, false);
  }

  public Challenge(
      String title,
      String description,
      ChallengeType type,
      Double targetValue,
      LocalDate startDate,
      LocalDate endDate,
      User creator,
      boolean official) {
    this.title = title;
    this.description = description;
    this.type = type;
    this.targetValue = targetValue;
    this.startDate = startDate;
    this.endDate = endDate;
    this.creator = creator;
    this.official = official;
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

  public boolean isOfficial() {
    return official;
  }

  public List<Sport> getSports() {
    return sports;
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

  public String getSportNames() {
    if (sports == null || sports.isEmpty()) {
      return "";
    }
    return sports.stream()
        .filter(Objects::nonNull)
        .map(sport -> sport.getName() == null ? null : sport.getName().name())
        .filter(Objects::nonNull)
        .reduce((first, second) -> first + ", " + second)
        .orElse("");
  }

  public boolean hasSportId(Long sportId) {
    if (sportId == null || sports == null || sports.isEmpty()) {
      return false;
    }
    return sports.stream().anyMatch(sport -> sport != null && sportId.equals(sport.getId()));
  }

  public boolean hasBadgeId(Long badgeId) {
    if (badgeId == null || badges == null || badges.isEmpty()) {
      return false;
    }
    return badges.stream().anyMatch(badge -> badge != null && badgeId.equals(badge.getId()));
  }

  public boolean hasParticipantId(Long userId) {
    if (userId == null || participants == null || participants.isEmpty()) {
      return false;
    }
    return participants.stream().anyMatch(user -> user != null && userId.equals(user.getId()));
  }

  public boolean isActive() {
    LocalDate today = LocalDate.now();
    boolean started = startDate == null || !today.isBefore(startDate);
    boolean notEnded = endDate == null || !today.isAfter(endDate);
    return started && notEnded;
  }

  public String getStartDateShortUs() {
    return formatShortDate(startDate);
  }

  public String getEndDateShortUs() {
    return formatShortDate(endDate);
  }

  public int getRemainingTimePercent() {
    if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
      return 0;
    }

    long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    if (totalDays <= 0) {
      return 0;
    }

    LocalDate today = LocalDate.now();
    long remainingDays;
    if (today.isBefore(startDate)) {
      remainingDays = totalDays;
    } else if (today.isAfter(endDate)) {
      remainingDays = 0;
    } else {
      remainingDays = ChronoUnit.DAYS.between(today, endDate) + 1;
    }

    double percent = (remainingDays * 100.0) / totalDays;
    return (int) Math.max(0, Math.min(100, Math.round(percent)));
  }

  public long getRemainingDays() {
    if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
      return 0;
    }
    LocalDate today = LocalDate.now();
    if (today.isBefore(startDate)) {
      return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    if (today.isAfter(endDate)) {
      return 0;
    }
    return ChronoUnit.DAYS.between(today, endDate) + 1;
  }

  private String formatShortDate(LocalDate date) {
    if (date == null) {
      return "--/--";
    }
    return date.format(SHORT_US_DATE);
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

  public void setOfficial(boolean official) {
    this.official = official;
  }

  public void setSports(List<Sport> sports) {
    this.sports = sports;
  }

  public void setBadges(List<Badge> badges) {
    this.badges = badges;
  }

  public void setParticipants(List<User> participants) {
    this.participants = participants;
  }
}
