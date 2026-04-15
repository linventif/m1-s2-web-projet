package web.sportflow.badge;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "badge")
public class Badge {

  private static final String DEFAULT_ICON_PATH = "/images/badge/running_5km.png";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(name = "icon_path", nullable = false, length = 255)
  private String iconPath = DEFAULT_ICON_PATH;

  public Badge() {}

  public Badge(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public Badge(String name, String description, String iconPath) {
    this.name = name;
    this.description = description;
    this.iconPath = iconPath;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getIconPath() {
    return iconPath == null || iconPath.isBlank() ? DEFAULT_ICON_PATH : iconPath;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIconPath(String iconPath) {
    this.iconPath = iconPath;
  }
}
