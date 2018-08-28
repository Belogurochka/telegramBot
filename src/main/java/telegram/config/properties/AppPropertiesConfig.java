package telegram.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties
public class AppPropertiesConfig {
	private BotProperty bot;
	private ProxyProperty proxy;
	private WeatherProperty weather;
	private PostProperty post;
}
