package telegram.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class YandexWeatherResponse {
	private String date;
	private String temp;
	private String condition;
	private String windSpeed;

	List<YandexForecast> yandexForecasts = new ArrayList<>();
}
