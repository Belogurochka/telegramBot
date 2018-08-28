package telegram.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.protocol.HttpContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import telegram.config.properties.AppPropertiesConfig;
import telegram.generated.russianPost.OperationHistoryRequest;
import telegram.service.Bot;
import telegram.service.BotService;
import telegram.service.RussianPostClientImpl;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import java.net.URI;

@Slf4j
@ComponentScan(basePackageClasses = BotService.class)
@SpringBootApplication
public class TelegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramApplication.class, args);
	}

	@Bean
	public Bot getBot(AppPropertiesConfig appPropertiesConfig) {
		try {
			ApiContextInitializer.init();
			TelegramBotsApi botsApi = new TelegramBotsApi();

			DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

			HttpHost httpHost = new HttpHost(appPropertiesConfig.getProxy().getHost(), appPropertiesConfig.getProxy().getPort());

			RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost).setAuthenticationEnabled(false).build();
			botOptions.setRequestConfig(requestConfig);

			Bot bot = new Bot(appPropertiesConfig.getBot().getToken(), appPropertiesConfig.getBot().getName(), botOptions);

			botsApi.registerBot(bot);
			log.info("Bot was registered");
			return bot;
		} catch (TelegramApiRequestException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	@Bean(name = "weatherRestTemplate")
	public RestTemplate weatherRestTemplate(AppPropertiesConfig appPropertiesConfig) {
		RestTemplate restTemplate = new RestTemplate(createWeatherClientHttpRequestFactory(appPropertiesConfig));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		return restTemplate;
	}

	@Bean
	@ConfigurationProperties
	AppPropertiesConfig appPropertiesConfig() {
		return new AppPropertiesConfig();
	}

	private BufferingClientHttpRequestFactory createWeatherClientHttpRequestFactory(AppPropertiesConfig appPropertiesConfig) {
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		httpComponentsClientHttpRequestFactory.setConnectTimeout(appPropertiesConfig.getWeather().getConnectionTimeout());
		httpComponentsClientHttpRequestFactory.setReadTimeout(appPropertiesConfig.getWeather().getReadTimeout());
		return new BufferingClientHttpRequestFactory(httpComponentsClientHttpRequestFactory);
	}

	@Bean
	RussianPostClientImpl znpCreateClient(AppPropertiesConfig appPropertiesConfig) {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(OperationHistoryRequest.class.getPackage().getName());

		RussianPostClientImpl russianPostClient = new RussianPostClientImpl();
		russianPostClient.setDefaultUri(appPropertiesConfig.getPost().getUrl());
		russianPostClient.setMarshaller(marshaller);
		russianPostClient.setUnmarshaller(marshaller);
		russianPostClient.setMessageFactory(getSaajSoapMessageFactory());
		russianPostClient.setMessageSender(
				createMessageSender(
						appPropertiesConfig.getPost().getLogin(),
						appPropertiesConfig.getPost().getPassword(),
						appPropertiesConfig.getPost().getConnectionTimeout(),
						appPropertiesConfig.getPost().getReadTimeout()));

		return russianPostClient;
	}

	private HttpComponentsMessageSender createMessageSender(String login, String password, int connectionTimeout, int readTimeout) {
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender() {
			@Override
			protected HttpContext createContext(URI uri) {
				if (StringUtils.isBlank(login)) {
					return null;
				}
				HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
				BasicScheme basicAuth = new BasicScheme();
				AuthCache authCache = new BasicAuthCache();
				authCache.put(target, basicAuth);
				CredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
				basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(login, password));
				HttpClientContext localContext = HttpClientContext.create();
				localContext.setCredentialsProvider(basicCredentialsProvider);
				localContext.setAuthCache(authCache);
				return localContext;
			}
		};
		messageSender.setConnectionTimeout(connectionTimeout);
		messageSender.setReadTimeout(readTimeout);
		return messageSender;
	}

	private SaajSoapMessageFactory getSaajSoapMessageFactory() {
		SaajSoapMessageFactory messageFactory = null;
		try {
			messageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
			messageFactory.setSoapVersion(SoapVersion.SOAP_12);
		} catch (SOAPException ex) {
			log.debug(ex.getMessage(), ex);
		}
		return messageFactory;
	}
}
