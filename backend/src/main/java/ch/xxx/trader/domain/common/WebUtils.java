package ch.xxx.trader.domain.common;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

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
	
//	public static boolean checkToken(HttpServletRequest request, JwtTokenProvider jwtTokenProvider) {
//		Optional<String> tokenStr = WebUtils.extractToken(Optional.ofNullable(request.getHeader(WebUtils.AUTHORIZATION)));		
//		Optional<Jws<Claims>> claims = jwtTokenProvider.getClaims(tokenStr);
//		if(claims.isPresent() && new Date().before(claims.get().getBody().getExpiration()) && claims.get().getBody().get("auth").toString().contains("USERS")) {
//			return true;
//		}
//		return false;
//	}
}
