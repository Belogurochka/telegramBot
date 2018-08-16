package telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.model.YandexWeatherResponse;

@Slf4j
public class Bot extends AbilityBot {

	private String botToken;
	private String botUsername;
	private YandexWeatherService yandexWeatherService;

	public Bot(String botToken, String botUsername, DefaultBotOptions options) {
		super(botToken, botUsername, options);
		this.botToken = botToken;
		this.botUsername = botUsername;
	}

	public int creatorId() {
		return 0;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			Message message = update.getMessage();
			if (message != null && message.hasText()) {
				SendMessage mess = new SendMessage().setChatId(update.getMessage().getChatId());
				switch (message.getText()) {
					case ("/help"): {
						mess.setText("Привет, я робот, могу исполнять команды:\n" +
								"/help - вывести эту справку\n" +
								"/прогноз - прогноз погоды в Москве на сутки");
						break;
					}
					case ("/прогноз"): {
						YandexWeatherResponse yandexWeatherResponse = yandexWeatherService.sendRequest();
						StringBuilder stringBuilder = new StringBuilder();
						yandexWeatherResponse.getYandexForecasts().forEach(forecast-> stringBuilder.append("Прогноз на ")
								.append(forecast.getDate())
								.append("\n")
								.append("Ночная температура - ")
								.append(forecast.getNight().getTemp())
								.append("\n")
								.append("Ночная скорость ветра - ")
								.append(forecast.getNight().getWindSpeed())
								.append("\n")
								.append("Ночная погода - ")
								.append(forecast.getNight().getCondition())
								.append("\n")
								.append("Дневная температура - ")
								.append(forecast.getDay().getTemp())
								.append("\n")
								.append("Дневная скорость ветра - ")
								.append(forecast.getDay().getWindSpeed())
								.append("\n")
								.append("Дневная погода - ")
								.append(forecast.getDay().getCondition())
								.append("\n"));
						mess.setText(stringBuilder.toString().replaceFirst("\n$",""));
						break;
					}
					default :{
						mess.setText("Неправильный код операции");
						break;
					}
				}

				if (StringUtils.isNotEmpty(mess.getText())) {
					try {
						execute(mess);
					} catch (TelegramApiException e) {
						log.error(e.getMessage());
						mess.setText("Извини, произошла ошибка");
					}
				}
			}
		}
	}


	@Override
	public String getBotUsername() {
		return botUsername;
	}


	@Override
	public String getBotToken() {
		return botToken;
	}

	@Autowired
	public void setYandexWeatherService(YandexWeatherService yandexWeatherService) {
		this.yandexWeatherService = yandexWeatherService;
	}
}
