package ru.home.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class Bot extends AbilityBot {

	private String botToken;
	private String botUsername;

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
		return botUsername;
	}


	@Override
	public String getBotToken() {
		return botToken;
	}
}
