package utc.miage.tp.weather;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record Weather(LocalDate date, String nom) {}
