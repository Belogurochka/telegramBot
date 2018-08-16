package telegram.service;

import telegram.model.WeatherResponse;

public interface WeatherService {

	WeatherResponse getCurrentWeather(String city);
	WeatherResponse getForecastWeather(String city, int daysCount);

}
