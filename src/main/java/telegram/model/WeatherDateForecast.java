package telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Data
public class WeatherDateForecast {
	private static String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern(ISO_FORMAT);

	private LocalDateTime date;
	private String temp;
	private String condition;
	private String windSpeed;

	@JsonProperty("dt_txt")
	private void unpackMain(String dt) {
		this.date = LocalDateTime.parse(dt, isoFormatter);
	}

	@JsonProperty("main")
	private void unpackMain(Map<String, Object> main) {
		this.temp = main.get("temp").toString();
	}

	@JsonProperty("weather")
	private void unpackWeather(List<Map<String, Object>> weather) {
		weather.stream().findFirst().ifPresent(w -> this.condition = (String) w.get("description"));
	}

	@JsonProperty("wind")
	private void unpackWind(Map<String, Object> wind) {
		this.windSpeed = wind.get("speed").toString();
	}

}
