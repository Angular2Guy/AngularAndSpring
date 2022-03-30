/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.trader.domain.common;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class JwtUtils {
	public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION.toLowerCase();
	public static final String TOKENAUTHKEY = "auth";
	public static final String TOKENLASTMSGKEY = "lastmsg";
	public static final String BEARER = "Bearer ";
	public static final String AUTHORITY = "authority";
	public static final String UUID = "uuid";
	public static final record TokenSubjectRole(String subject, String role) {}

	public static Optional<String> extractToken(Map<String,String> headers) {
		String authStr = headers.get(AUTHORIZATION);
		return extractToken(Optional.ofNullable(authStr));
	}

	private static Optional<String> extractToken(Optional<String> authStr) {
		if (authStr.isPresent()) {
			authStr = Optional.ofNullable(authStr.get().startsWith(BEARER) ? authStr.get().substring(7) : null);
		}
		return authStr;
	}

	public static Optional<String> resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith(JwtUtils.BEARER)) {
			return Optional.of(bearerToken.substring(7, bearerToken.length()));
		}
		return Optional.empty();
	}
	
	public static Optional<Jws<Claims>> getClaims(Optional<String> token, Key jwtTokenKey) {
		if (!token.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(Jwts.parserBuilder().setSigningKey(jwtTokenKey).build().parseClaimsJws(token.get()));
	}
	
	public static String getTokenRoles(Map<String,String> headers, Key jwtTokenKey) {
		Optional<String> tokenStr = extractToken(headers);
		Optional<Jws<Claims>> claims = JwtUtils.getClaims(tokenStr, jwtTokenKey);
		if (claims.isPresent() && new Date().before(claims.get().getBody().getExpiration())) {
			return claims.get().getBody().get(TOKENAUTHKEY).toString();
		}
		return "";
	}

	public static TokenSubjectRole getTokenUserRoles(Map<String,String> headers,
			Key jwtTokenKey) {
		Optional<String> tokenStr = extractToken(headers);
		Optional<Jws<Claims>> claims = JwtUtils.getClaims(tokenStr, jwtTokenKey);
		if (claims.isPresent() && new Date().before(claims.get().getBody().getExpiration())) {
			return new TokenSubjectRole(claims.get().getBody().getSubject(),
					claims.get().getBody().get(TOKENAUTHKEY).toString());
		}
		return new TokenSubjectRole(null, null);
	}

	public static boolean checkToken(HttpServletRequest request, Key jwtTokenKey) {
		Optional<String> tokenStr = JwtUtils
				.extractToken(Optional.ofNullable(request.getHeader(JwtUtils.AUTHORIZATION)));
		Optional<Jws<Claims>> claims = JwtUtils.getClaims(tokenStr, jwtTokenKey);
		if (claims.isPresent() && new Date().before(claims.get().getBody().getExpiration())
				&& claims.get().getBody().get(TOKENAUTHKEY).toString().contains(Role.USERS.name())
				&& !claims.get().getBody().get(TOKENAUTHKEY).toString().contains(Role.GUEST.name())) {
			return true;
		}
		return false;
	}
}
