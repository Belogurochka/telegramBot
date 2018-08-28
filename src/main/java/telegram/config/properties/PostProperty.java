package telegram.config.properties;

import lombok.Data;

@Data
public class PostProperty {
	private int connectionTimeout;
	private int readTimeout;
	private String login;
	private String url;
	private String password;
}
