package utc.miage.tp.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;

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
    @JsonProperty("wind_speed_10m") String windSpeed10m,
    @JsonProperty("weather_code") String weatherCode) {}

record Hourly(
    List<String> time,
    @JsonProperty("temperature_2m") List<Double> temperature2m,
    @JsonProperty("apparent_temperature") List<Double> apparentTemperature,
    List<Double> precipitation,
    @JsonProperty("wind_speed_10m") List<Double> windSpeed10m,
    @JsonProperty("weather_code") List<String> weatherCode) {

  /**
   * * Finds the data for a specific ISO-8601 date-hour string. Example input: "2023-10-27T14:00"
   */
  public Optional<HourlyDataPoint> getDataAtTime(String dateTime) {
    int index = time.indexOf(dateTime);

    if (index == -1) {
      return Optional.empty();
    }

    return Optional.of(
        new HourlyDataPoint(
            time.get(index),
            temperature2m.get(index),
            apparentTemperature.get(index),
            precipitation.get(index),
            windSpeed10m.get(index),
            weatherCode.get(index)));
  }
}

/** * A simple DTO to wrap a single hour's worth of data. */
record HourlyDataPoint(
    String time,
    double temperature,
    double apparentTemperature,
    double precipitation,
    double windSpeed,
    String weatherCode) {} // Added weatherCode here to fix the constructor error
