package utc.miage.tp.sport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;

@Entity
@Table(name = "sport")
public class Sport {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "calPerMin", nullable = false)
  private Double calPerMin;

  public Sport() {

  }
  public Sport(String name, Double calPerMin) {
    this.name = name;
    this.calPerMin = calPerMin;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getID() {
    return this.id;
  }

  public void setID(Long id) {
    this.id = id;
  }

  public Double getCaloryPerMinutes() {
    return this.calPerMin;
  }

  public void setCaloryPerMinutes(Double calPerMin) {
    this.calPerMin = calPerMin;
  }
}
