package ru.home.telegram;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@Slf4j
@SpringBootApplication
public class TelegramApplication {

	private static final String BOT_NAME = "MyFirstWeatherBot";
	private static final String BOT_TOKEN = "658153349:AAGeJ2Ve88cQHo25M5gCMPBNjpQWaRdJUmg" /* your bot's token here */;
	private static final String PROXY_HOST = "195.201.124.134" /* proxy host */;
	private static final Integer PROXY_PORT = 3128 /* proxy port */;


	public static void main(String[] args) {
		try {
			registerProxyBot();
			SpringApplication.run(TelegramApplication.class, args);
		} catch (TelegramApiRequestException ex) {
			log.error(ex.getMessage());
		}
	}

	private static void registerProxyBot() throws TelegramApiRequestException {
		ApiContextInitializer.init();
		TelegramBotsApi botsApi = new TelegramBotsApi();

		DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

		HttpHost httpHost = new HttpHost(PROXY_HOST, PROXY_PORT);

		RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(false).build();
		botOptions.setRequestConfig(requestConfig);

		Bot bot = new Bot(BOT_TOKEN, BOT_NAME, botOptions);

		botsApi.registerBot(bot);
	}
}
