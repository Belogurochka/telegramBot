package telegram.model;

import lombok.Data;

@Data
public class YandexForecast {
	private String date;
	private YandexWeatherParams night;
	private YandexWeatherParams day;
}
