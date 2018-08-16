package telegram.service;

import telegram.config.properties.AppPropertiesConfig;

public interface BotService {
	void initialize(AppPropertiesConfig appPropertiesConfig);
}
