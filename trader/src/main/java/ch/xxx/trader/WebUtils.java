package ch.xxx.trader;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;

public class WebUtils {
	public static WebClient buildWebClient(String url) {
		ReactorClientHttpConnector connector =
	            new ReactorClientHttpConnector(options ->
	                    options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000));
		return WebClient.builder().clientConnector(connector).baseUrl(url).build();
	}
}
