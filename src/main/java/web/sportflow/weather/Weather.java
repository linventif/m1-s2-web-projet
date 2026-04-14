package web.sportflow.weather;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public record Weather(LocalDate date, String nom) {}
