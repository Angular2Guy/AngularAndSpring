package ch.xxx.trader.domain.common;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.SslProvider.SslContextSpec;

public class WebUtils {

	public static final String LASTOBCALLBF = "LAST_ORDERBOOK_CALL_BITFINEX";
	public static final String LASTOBCALLBS = "LAST_ORDERBOOK_CALL_BITSTAMP";
	public static final String LASTOBCALLIB = "LAST_ORDERBOOK_CALL_ITBIT";
	public static final String SECURITYCONTEXT = "SPRING_SECURITY_CONTEXT";
	public static final String AUTHORIZATION = "authorization";

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
		ReactorClientHttpConnector connector = new ReactorClientHttpConnector();
		return WebClient.builder().clientConnector(connector).baseUrl(url).build();
	}
	
	public static WebClient createWebClient() {
		ConnectionProvider provider = ConnectionProvider.builder("Client").maxConnections(20)
				.maxIdleTime(Duration.ofSeconds(6)).maxLifeTime(Duration.ofSeconds(7))
				.pendingAcquireTimeout(Duration.ofSeconds(9L)).evictInBackground(Duration.ofSeconds(10)).build();

		WebClient webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(HttpClient
				.create(provider).secure(spec -> sslTimeouts(spec)).option(ChannelOption.SO_KEEPALIVE, false)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
				.doOnConnected(
						c -> c.addHandlerLast(new ReadTimeoutHandler(6)).addHandlerLast(new WriteTimeoutHandler(7)))
				.responseTimeout(Duration.ofSeconds(7L)))).build();
		return webClient;
	}

	private static void sslTimeouts(SslContextSpec spec) {
		try {
			spec.sslContext(SslContextBuilder.forClient().build()).handshakeTimeout(Duration.ofSeconds(8))
					.closeNotifyFlushTimeout(Duration.ofSeconds(6)).closeNotifyReadTimeout(Duration.ofSeconds(6));
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Optional<String> extractToken(Map<String,String> headers) {
		String authStr = headers.get(AUTHORIZATION);
		return extractToken(Optional.ofNullable(authStr));
	}
	
	private static Optional<String> extractToken(Optional<String> authStr) {		
		if(authStr.isPresent()) {
			authStr = Optional.ofNullable(authStr.get().startsWith("Bearer ") ? authStr.get().substring(7) : null);
		}
		return authStr;
	}
}
