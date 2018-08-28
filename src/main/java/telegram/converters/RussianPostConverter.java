package telegram.converters;

import telegram.generated.russianPost.OperationHistoryData;
import telegram.generated.russianPost.OperationHistoryRequest;
import telegram.model.RussianPostRequest;
import telegram.model.RussianPostResponse;

public interface RussianPostConverter {

	OperationHistoryRequest convertRequest(RussianPostRequest request);

	RussianPostResponse convertResponse(OperationHistoryData response);

}
