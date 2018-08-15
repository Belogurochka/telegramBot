package ru.home.telegram.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.home.telegram.config.properties.AppPropertiesConfig;
import ru.home.telegram.service.Bot;

@Slf4j
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
			return bot;
		} catch (TelegramApiRequestException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Bean
	@ConfigurationProperties
	AppPropertiesConfig appPropertiesConfig() {
		return new AppPropertiesConfig();
	}
}
