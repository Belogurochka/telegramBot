package telegram.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegram.config.properties.AppPropertiesConfig;
import telegram.model.WeatherDateForecast;
import telegram.model.WeatherResponse;
import telegram.service.WeatherService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherServiceImpl implements WeatherService {
	private RestTemplate restTemplate;
	private AppPropertiesConfig appPropertiesConfig;
	private static final String CURRENT_WEATHER_URL = "weather";
	private static final String FORECAST_WEATHER_URL = "forecast";

	@Override
	public WeatherResponse getCurrentWeather(String city) {
		if (StringUtils.isEmpty(city)) return null;
		ResponseEntity<WeatherResponse> response = restTemplate.exchange(generateUrl(CURRENT_WEATHER_URL, city), HttpMethod.GET, null, WeatherResponse.class);
		WeatherResponse weatherResponse = response.getBody();
		weatherResponse.getList().stream().findFirst().ifPresent(weather -> weather.setDate(LocalDateTime.now()));
		return weatherResponse;
	}

	@Override
	public WeatherResponse getForecastWeather(String city, int daysCount) {
		if (StringUtils.isEmpty(city)) return null;
		ResponseEntity<WeatherResponse> response = restTemplate.exchange(generateUrl(FORECAST_WEATHER_URL, city), HttpMethod.GET, null, WeatherResponse.class);
		return processForecastWeatherResponse(response.getBody(), daysCount);
	}

	private WeatherResponse processForecastWeatherResponse(WeatherResponse response, int daysCount) {
		if (response == null) return null;
		List<WeatherDateForecast> daysForecast = response.getList()
				.stream()
				.filter(forecast -> forecast.getDate().getHour() == 12)
				.sorted(Comparator.comparing(WeatherDateForecast::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
				.limit(daysCount)
				.collect(Collectors.toList());
		response.setList(daysForecast);
		return response;
	}

	private String generateUrl(String urlPrefix, String city) {
		String baseUrl = appPropertiesConfig.getWeather().getUrl();
		return baseUrl +
				urlPrefix +
				"?" +
				"q=" + city +
				"&" +
				"appid=" +
				appPropertiesConfig.getWeather().getToken() +
				"&units=metric&lang=ru";
	}

	@Autowired
	@Qualifier("weatherRestTemplate")
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	public void setAppPropertiesConfig(AppPropertiesConfig appPropertiesConfig) {
		this.appPropertiesConfig = appPropertiesConfig;
	}
}
