package web.sportflow.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import web.sportflow.user.User;

@Entity
@Table(name = "notifications")
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "recipient_user_id", nullable = false)
  private User recipient;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "actor_user_id")
  private User actor;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private NotificationType type;

  @Column(nullable = false, length = 280)
  private String message;

  @Column(name = "target_url", length = 512)
  private String targetUrl;

  @Column(name = "read_flag", nullable = false)
  private boolean read;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public Notification() {}

  public Notification(
      User recipient, User actor, NotificationType type, String message, String targetUrl) {
    this.recipient = recipient;
    this.actor = actor;
    this.type = type;
    this.message = message;
    this.targetUrl = targetUrl;
    this.read = false;
  }

  @PrePersist
  public void prePersist() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public User getRecipient() {
    return recipient;
  }

  public User getActor() {
    return actor;
  }

  public NotificationType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String getTargetUrl() {
    return targetUrl;
  }

  public boolean isRead() {
    return read;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setRecipient(User recipient) {
    this.recipient = recipient;
  }

  public void setActor(User actor) {
    this.actor = actor;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setTargetUrl(String targetUrl) {
    this.targetUrl = targetUrl;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
