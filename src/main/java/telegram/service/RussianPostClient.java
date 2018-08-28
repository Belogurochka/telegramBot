package telegram.service;


import telegram.generated.russianPost.OperationHistoryData;
import telegram.generated.russianPost.OperationHistoryRequest;

public interface RussianPostClient {
	OperationHistoryData getOperationHistory(OperationHistoryRequest request);
}
