package telegram.service;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import telegram.generated.russianPost.OperationHistoryData;
import telegram.generated.russianPost.OperationHistoryRequest;

public class RussianPostClientImpl extends WebServiceGatewaySupport implements RussianPostClient {
	@Override
	public OperationHistoryData getOperationHistory(OperationHistoryRequest request) {
		return (OperationHistoryData) getWebServiceTemplate().marshalSendAndReceive(getDefaultUri(), request);
	}
}
