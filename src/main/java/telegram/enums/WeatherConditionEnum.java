package telegram.enums;

import java.util.HashMap;
import java.util.Map;

public enum WeatherConditionEnum {
	CLEAR("clear", "ясно"),
	PARTLY_CLOUDY("partly-cloudy", "малооблачно"),
	CLOUDY("cloudy", "облачно с прояснениями"),
	OVERCAST("overcast", "пасмурно"),
	PARTLY_CLOUDY_AND_LIGHT_RAIN("partly-cloudy-and-light-rain", "небольшой дождь"),
	PARTLY_CLOUDY_AND_RAIN("partly-cloudy-and-rain", "дождь"),
	OVERCAST_AND_RAIN("overcast-and-rain", "сильный дождь"),
	OVERCAST_THUNDER_STORMS_WITH_RAIN("overcast-thunderstorms-with-rain", "сильный дождь, гроза"),
	CLOUDY_AND_LIGHT_RAIN("cloudy-and-light-rain", "небольшой дождь"),
	OVERCAST_AND_LIGHT_RAIN("overcast-and-light-rain", "небольшой дождь"),
	CLOUDY_AND_RAIN("cloudy-and-rain", "дождь"),
	OVERCAST_AND_WET_SNOW("overcast-and-wet-snow", "дождь со снегом"),
	PARTLY_CLOUDY_AND_LIGHT_SNOW("partly-cloudy-and-light-snow", "небольшой снег"),
	PARTLY_CLOUDY_AND_SNOW("partly-cloudy-and-snow", "снег"),
	OVERCAST_AND_SNOW("overcast-and-snow", "снегопад"),
	CLOUDY_AND_LIGHT_SNOW("cloudy-and-light-snow", "небольшой снег"),
	OVERCAST_AND_LIGHT_SNOW("overcast-and-light-snow", "небольшой снег"),
	CLOUDY_AND_SNOW("cloudy-and-snow", "снег");

	private String condition;
	private String description;

	WeatherConditionEnum(String condition, String description) {
		this.condition = condition;
		this.description = description;
	}

	private static final Map<String, WeatherConditionEnum> map;

	static {
		map = new HashMap<>();
		for (WeatherConditionEnum v : WeatherConditionEnum.values()) {
			map.put(v.condition, v);
		}
	}

	public static String getDescription(String condition) {
		return map.get(condition).description;
	}


	public String getCondition() {
		return condition;
	}
}
