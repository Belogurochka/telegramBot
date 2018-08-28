package telegram.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RussianPostRequest implements Serializable {
	private String barCode;
	private int messageType = 0;
	private String language = "RUS";
}
