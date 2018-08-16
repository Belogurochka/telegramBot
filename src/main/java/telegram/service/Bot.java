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
import telegram.model.WeatherResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Bot extends AbilityBot {

	private String botToken;
	private String botUsername;
	private WeatherService weatherService;

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
				if (message.getText().matches("^/help")) {
					mess.setText("Привет, я робот, могу исполнять команды:\n" +
							"/help - вывести эту справку\n" +
							"/forecast - прогноз погоды, /forecast {Город} {кол-во дней} ");
				} else if (message.getText().matches("^/forecast.*")) {
					String[] splited = message.getText().split("\\s+");
					String city=null;
					String daysCount=null;
					WeatherResponse weatherResponse;
					if (splited.length==1) {
						mess.setText("Необходимо ввести город для прогноза");
					} else {
						city = splited[1];
						if (splited.length>2) {
							daysCount=splited[2];
						}
					}
					if (StringUtils.isEmpty(daysCount) || Integer.valueOf(daysCount) < 1) {
						weatherResponse = weatherService.getCurrentWeather(city);
					} else {
						weatherResponse = weatherService.getForecastWeather(city, Integer.valueOf(daysCount));
					}
					StringBuilder stringBuilder = new StringBuilder();
					weatherResponse.getList().forEach(forecast -> stringBuilder.append("Прогноз на ")
							.append(forecast.getDate().toString().replace("T",""))
							.append("\n")
							.append("Температура - ")
							.append(forecast.getTemp()).append("°")
							.append("\n")
							.append("Cкорость ветра - ")
							.append(forecast.getWindSpeed()).append(" м/с")
							.append("\n")
							.append("Погода - ")
							.append(forecast.getCondition())
							.append("\n")
							.append("\n"));
					mess.setText(stringBuilder.toString().replaceFirst("\n\n$", ""));
				} else {
					mess.setText("Неправильный код операции");
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
	public void setWeatherService(WeatherService weatherService) {
		this.weatherService = weatherService;
	}
}
