package ru.home.telegram;

import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class Bot extends AbilityBot {

	protected Bot(String botToken, String botUsername, DefaultBotOptions options) {
		super(botToken, botUsername, options);
	}

	public int creatorId() {
		return 0;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			Message message = update.getMessage();
			if (message != null && message.hasText() && message.getText().equals("/help")) {
				SendMessage mess = new SendMessage()
						.setChatId(update.getMessage().getChatId())
						.setText("Привет, я робот");
				try {
					execute(mess);
				} catch (TelegramApiException e) {
					log.error(e.getMessage());
				}
			}
		}
	}


	@Override
	public String getBotUsername() {
		return "MyFirstWeatherBot";
	}


	@Override
	public String getBotToken() {
		return "658153349:AAGeJ2Ve88cQHo25M5gCMPBNjpQWaRdJUmg";
	}
}
