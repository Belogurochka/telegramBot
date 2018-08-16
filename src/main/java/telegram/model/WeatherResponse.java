package telegram.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
	private String cod;
	private Double message;
	private Integer cnt;
	@JsonProperty(value="list")
	List<WeatherDateForecast> list = new ArrayList<>();
}
