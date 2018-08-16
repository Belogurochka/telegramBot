package telegram.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegram.config.properties.AppPropertiesConfig;
import telegram.enums.WeatherConditionEnum;
import telegram.model.YandexForecast;
import telegram.model.YandexWeatherParams;
import telegram.model.YandexWeatherResponse;
import telegram.service.YandexWeatherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class YandexWeatherServiceImpl implements YandexWeatherService {
	private RestTemplate restTemplate;
	private AppPropertiesConfig appPropertiesConfig;
	private static final String FACT_WEATHER="fact";
	private static final String TEMPERATURE="temp";
	private static final String WIND_SPEED="wind_speed";
	private static final String CONDITION="condition";
	private static final String FORECASTS="forecasts";
	private static final String DATE="date";
	private static final String PARTS="parts";
	private static final String NIGHT="night";
	private static final String DAY="day";
	private static final String TEMPERATURE_MIN="temp_min";

	@Override
	public YandexWeatherResponse sendRequest() {
		HttpEntity entity = new HttpEntity<>(null, getHeaders());
		ParameterizedTypeReference<Map<String, Object>> responseType = new ParameterizedTypeReference<Map<String, Object>>() {
		};
		ResponseEntity<Map<String, Object>> response = restTemplate.exchange(generateUrl(), HttpMethod.GET, entity, responseType);
		return convertYandexResponse(response);
	}

	private YandexWeatherResponse convertYandexResponse(ResponseEntity<Map<String, Object>> response) {
		Map<String, Object> responseMap = response.getBody();
		YandexWeatherResponse yandexWeatherResponse = new YandexWeatherResponse();
		Map<String, Object> factWeather = (Map<String, Object>) responseMap.get(FACT_WEATHER);
		yandexWeatherResponse.setTemp(factWeather.get(TEMPERATURE).toString());
		yandexWeatherResponse.setWindSpeed(factWeather.get(WIND_SPEED).toString());
		yandexWeatherResponse.setCondition(WeatherConditionEnum.getDescription(factWeather.get(CONDITION).toString()));

		List<Map<String, Object>> forecasts = (List<Map<String, Object>>) responseMap.get(FORECASTS);
		forecasts.forEach(forecast -> {
			YandexForecast yandexForecast = new YandexForecast();
			yandexForecast.setDate(forecast.get(DATE).toString());
			Map<String, Object> parts = (Map<String, Object>) forecast.get(PARTS);
			Map<String, Object> night = (Map<String, Object>) parts.get(NIGHT);

			YandexWeatherParams nightParams = new YandexWeatherParams();
			nightParams.setTemp(night.get(TEMPERATURE_MIN).toString());
			nightParams.setWindSpeed(night.get(WIND_SPEED).toString());
			nightParams.setCondition(WeatherConditionEnum.getDescription(night.get(CONDITION).toString()));
			yandexForecast.setNight(nightParams);

			Map<String, Object> day = (Map<String, Object>) parts.get(DAY);
			YandexWeatherParams dayParams = new YandexWeatherParams();
			dayParams.setTemp(day.get(TEMPERATURE_MIN).toString());
			dayParams.setWindSpeed(day.get(WIND_SPEED).toString());
			dayParams.setCondition(WeatherConditionEnum.getDescription(day.get(CONDITION).toString()));
			yandexForecast.setDay(dayParams);

			yandexWeatherResponse.getYandexForecasts().add(yandexForecast);
		});
		return yandexWeatherResponse;
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		ArrayList<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		headers.setAccept(mediaTypes);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("X-Yandex-API-Key", appPropertiesConfig.getYandex().getToken());
		return headers;
	}

	private String generateUrl() {
		String baseUrl = appPropertiesConfig.getYandex().getUrl();
		return baseUrl +
				"?" +
				"lat=" +
				appPropertiesConfig.getYandex().getLat() +
				"&" +
				"lon=" +
				appPropertiesConfig.getYandex().getLon();
	}

	@Autowired
	@Qualifier("yandexRestTemplate")
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Autowired
	public void setAppPropertiesConfig(AppPropertiesConfig appPropertiesConfig) {
		this.appPropertiesConfig = appPropertiesConfig;
	}
}
