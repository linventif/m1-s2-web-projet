package utc.miage.tp.weather;

import jakarta.persistence.Embeddable;

@Embeddable
public class WeatherStatsDTO {
  String averageTemperature;
  String maxTemperature;
  String minTemperature;
  String averageApparentTemperature;
  String averagePrecipitation;
  String averageWindSpeed;
  String weatherIndicator;

  public WeatherStatsDTO() {
  }

  public WeatherStatsDTO(
      String averageTemperature,
      String maxTemperature,
      String minTemperature,
      String averageApparentTemperature,
      String averagePrecipitation,
      String averageWindSpeed,
      String weatherIndicator) {
    this.averageTemperature = averageTemperature;
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.averageApparentTemperature = averageApparentTemperature;
    this.averagePrecipitation = averagePrecipitation;
    this.averageWindSpeed = averageWindSpeed;
    this.weatherIndicator = weatherIndicator;
  }

  public String getAverageTemperature() {
    return averageTemperature;
  }

  public void setAverageTemperature(String averageTemperature) {
    this.averageTemperature = averageTemperature;
  }

  public String getMaxTemperature() {
    return maxTemperature;
  }

  public void setMaxTemperature(String maxTemperature) {
    this.maxTemperature = maxTemperature;
  }

  public String getMinTemperature() {
    return minTemperature;
  }

  public void setMinTemperature(String minTemperature) {
    this.minTemperature = minTemperature;
  }

  public String getAverageApparentTemperature() {
    return averageApparentTemperature;
  }

  public void setAverageApparentTemperature(String averageApparentTemperature) {
    this.averageApparentTemperature = averageApparentTemperature;
  }

  public String getAveragePrecipitation() {
    return averagePrecipitation;
  }

  public void setAveragePrecipitation(String averagePrecipitation) {
    this.averagePrecipitation = averagePrecipitation;
  }

  public String getAverageWindSpeed() {
    return averageWindSpeed;
  }

  public void setAverageWindSpeed(String averageWindSpeed) {
    this.averageWindSpeed = averageWindSpeed;
  }

  public String getWeatherIndicator() {
    return weatherIndicator;
  }

  public void setWeatherIndicator(String weatherIndicator) {
    this.weatherIndicator = weatherIndicator;
  }
}
