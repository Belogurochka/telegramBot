package telegram.converters;

import telegram.generated.russianPost.OperationHistoryData;
import telegram.generated.russianPost.OperationHistoryRequest;
import telegram.model.RussianPostRequest;
import telegram.model.RussianPostResponse;

public class RussianPostConverterImpl implements RussianPostConverter {
	@Override
	public OperationHistoryRequest convertRequest(RussianPostRequest request) {
		OperationHistoryRequest operationHistoryRequest = new OperationHistoryRequest();
		operationHistoryRequest.setBarcode(request.getBarCode());
		operationHistoryRequest.setLanguage(request.getLanguage());
		operationHistoryRequest.setMessageType(request.getMessageType());
		return operationHistoryRequest;
	}

	@Override
	public RussianPostResponse convertResponse(OperationHistoryData response) {
		return null;
	}
}
