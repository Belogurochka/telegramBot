package telegram.model;

import lombok.Data;

@Data
public class YandexWeatherParams {
	private String temp;
	private String condition;
	private String windSpeed;
}
