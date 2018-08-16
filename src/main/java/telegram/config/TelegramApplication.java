package telegram.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import telegram.config.properties.AppPropertiesConfig;
import telegram.service.Bot;
import telegram.service.BotService;

@Slf4j
@ComponentScan(basePackageClasses = BotService.class)
@SpringBootApplication
public class TelegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramApplication.class, args);
	}

	@Bean
	public Bot getBot(AppPropertiesConfig appPropertiesConfig) {
		try {
			ApiContextInitializer.init();
			TelegramBotsApi botsApi = new TelegramBotsApi();

			DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

			HttpHost httpHost = new HttpHost(appPropertiesConfig.getProxy().getHost(), appPropertiesConfig.getProxy().getPort());

			RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(false).build();
			botOptions.setRequestConfig(requestConfig);

			Bot bot = new Bot(appPropertiesConfig.getBot().getToken(), appPropertiesConfig.getBot().getName(), botOptions);

			botsApi.registerBot(bot);
			log.info("Bot was registered");
			return bot;
		} catch (TelegramApiRequestException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Bean(name = "weatherRestTemplate")
	public RestTemplate weatherRestTemplate(AppPropertiesConfig appPropertiesConfig) {
		RestTemplate restTemplate = new RestTemplate(createWeatherClientHttpRequestFactory(appPropertiesConfig));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

	@Bean
	@ConfigurationProperties
	AppPropertiesConfig appPropertiesConfig() {
		return new AppPropertiesConfig();
	}

	private BufferingClientHttpRequestFactory createWeatherClientHttpRequestFactory(AppPropertiesConfig appPropertiesConfig) {
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpComponentsClientHttpRequestFactory.setConnectTimeout(appPropertiesConfig.getWeather().getConnectionTimeout());
		httpComponentsClientHttpRequestFactory.setReadTimeout(appPropertiesConfig.getWeather().getReadTimeout());
		return new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory);
	}
}
