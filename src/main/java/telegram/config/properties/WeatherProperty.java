package telegram.config.properties;

import lombok.Data;

@Data
public class WeatherProperty {
	private int connectionTimeout;
	private int readTimeout;
	private String token;
	private String url;
}
