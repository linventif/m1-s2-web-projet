package utc.miage.tp.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponseDTO {

  private List<CityResultDTO> results;

  public List<CityResultDTO> getResults() {
    return results;
  }

  public void setResults(List<CityResultDTO> results) {
    this.results = results;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CityResultDTO {
    private double latitude;
    private double longitude;

    public double getLatitude() {
      return latitude;
    }

    public void setLatitude(double latitude) {
      this.latitude = latitude;
    }

    public double getLongitude() {
      return longitude;
    }

    public void setLongitude(double longitude) {
      this.longitude = longitude;
    }
  }
}
