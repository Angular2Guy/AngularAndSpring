package ch.xxx.trader;

import java.time.Duration;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;

public class WebUtils {

	public static final String LASTOBCALLBF = "LAST_ORDERBOOK_CALL_BITFINEX";
	public static final String LASTOBCALLBS = "LAST_ORDERBOOK_CALL_BITSTAMP";
	public static final String LASTOBCALLIB = "LAST_ORDERBOOK_CALL_ITBIT";
	public static final String SECURITYCONTEXT = "SPRING_SECURITY_CONTEXT";

	public static boolean checkOBRequest(HttpServletRequest request, String sessionKey) {
		Instant last = (Instant) request.getSession().getAttribute(sessionKey);
		Duration dur = Duration.ofSeconds(10);
		Instant next = last == null ? Instant.now() : last.plus(dur);
		Instant now = Instant.now();
		if (last == null || now.isAfter(next)) {
			request.getSession().setAttribute(sessionKey, now);
			return true;
		}
		return false;
	}

	public static WebClient buildWebClient(String url) {
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector(
				options -> options.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000));
		return WebClient.builder().clientConnector(connector).baseUrl(url).build();
	}

}
