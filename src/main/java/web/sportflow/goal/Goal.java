package web.sportflow.goal;

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
import web.sportflow.user.User;

@Entity
@Table(name = "goal")
public class Goal {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String label;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GoalType type;

  @Column(nullable = false)
  private Double targetValue;

  @Column(nullable = false)
  private Double currentValue = 0.0;

  @Column(nullable = false)
  private String unit;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  public Goal() {}

  public Goal(
      String label,
      GoalType type,
      Double targetValue,
      Double currentValue,
      String unit,
      User user) {
    this.label = label;
    this.type = type;
    this.targetValue = targetValue;
    this.currentValue = currentValue;
    this.unit = unit;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public GoalType getType() {
    return type;
  }

  public Double getTargetValue() {
    return targetValue;
  }

  public Double getCurrentValue() {
    return currentValue;
  }

  public String getUnit() {
    return unit;
  }

  public User getUser() {
    return user;
  }

  public int getProgressPercent() {
    if (targetValue == null || targetValue == 0) {
      return 0;
    }
    return (int) Math.min(100, Math.round((currentValue / targetValue) * 100));
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setType(GoalType type) {
    this.type = type;
  }

  public void setTargetValue(Double targetValue) {
    this.targetValue = targetValue;
  }

  public void setCurrentValue(Double currentValue) {
    this.currentValue = currentValue;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
