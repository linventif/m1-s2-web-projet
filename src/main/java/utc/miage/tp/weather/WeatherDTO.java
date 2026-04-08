package utc.miage.tp.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherDTO(
    double latitude,
    double longitude,
    @JsonProperty("generationtime_ms") double generationTimeMs,
    @JsonProperty("utc_offset_seconds") int utcOffsetSeconds,
    String timezone,
    @JsonProperty("timezone_abbreviation") String timezoneAbbreviation,
    double elevation,
    @JsonProperty("hourly_units") HourlyUnits hourlyUnits,
    Hourly hourly) {}

record HourlyUnits(
    String time,
    @JsonProperty("temperature_2m") String temperature2m,
    @JsonProperty("apparent_temperature") String apparentTemperature,
    String precipitation,
    @JsonProperty("wind_speed_10m") String windSpeed10m) {}

record Hourly(
    List<String> time,
    @JsonProperty("temperature_2m") List<Double> temperature2m,
    @JsonProperty("apparent_temperature") List<Double> apparentTemperature,
    List<Double> precipitation,
    @JsonProperty("wind_speed_10m") List<Double> windSpeed10m) {}
