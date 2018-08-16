package telegram.config.properties;

import lombok.Data;

@Data
public class YandexProperty {
	private int connectionTimeout;
	private int readTimeout;
	private String token;
	private String url;
	private String lat;
	private String lon;
}
