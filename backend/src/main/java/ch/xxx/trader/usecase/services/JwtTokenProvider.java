/**
 *    Copyright 2016 Sven Loesekann

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
package ch.xxx.trader.usecase.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import ch.xxx.trader.domain.common.JwtUtils;
import ch.xxx.trader.domain.common.Role;
import ch.xxx.trader.domain.exceptions.AuthenticationException;
import ch.xxx.trader.domain.exceptions.JwtTokenValidationException;
import ch.xxx.trader.domain.model.entity.RevokedToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);
	@Value("${security.jwt.token.secret-key}")
	private String secretKey;

	@Value("${security.jwt.token.expire-length}")
	private long validityInMilliseconds; // 24h

	private final List<UserNameUuid> loggedOutUsers = new CopyOnWriteArrayList<>();

	public record UserNameUuid(String userName, String uuid) {
	}

	private Key jwtTokenKey;

	@PostConstruct
	public void init() {
		this.jwtTokenKey = Keys
				.hmacShaKeyFor(Base64.getUrlDecoder().decode(secretKey.getBytes(StandardCharsets.ISO_8859_1)));
	}

	public void updateLoggedOutUsers(List<RevokedToken> users) {
		this.loggedOutUsers.clear();
		this.loggedOutUsers
				.addAll(users.stream().map(myUser -> new UserNameUuid(myUser.getName(), myUser.getUuid())).toList());
		LOG.info("updateLoggedOutUsers: {}", this.loggedOutUsers.size());
	}

	public String createToken(String username, List<Role> roles) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put(JwtUtils.TOKENAUTHKEY, roles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority()))
				.filter(Objects::nonNull).collect(Collectors.toList()));
		claims.put(JwtUtils.TOKENLASTMSGKEY, new Date().getTime());
		claims.put(JwtUtils.UUID, UUID.randomUUID().toString());
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(validity)
				.signWith(this.jwtTokenKey, SignatureAlgorithm.HS256).compact();
	}

	public Optional<Jws<Claims>> getClaims(Optional<String> token) {
		if (!token.isPresent()) {
			return Optional.empty();
		}
		return Optional.of(Jwts.parserBuilder().setSigningKey(this.jwtTokenKey).build().parseClaimsJws(token.get()));
	}

	public UsernamePasswordAuthenticationToken getUserAuthenticationToken(String token) {
		if (this.getAuthorities(token).stream().filter(role -> role.equals(Role.GUEST)).count() > 0) {
			return new UsernamePasswordAuthenticationToken(this.getUsername(token), null);
		}
		return new UsernamePasswordAuthenticationToken(this.getUsername(token), "", this.getAuthorities(token));
	}

	@SuppressWarnings("unchecked")
	public Collection<Role> getAuthorities(String token) {
		Collection<Role> roles = new LinkedList<>();
		for (Role role : Role.values()) {
			roles.add(role);
		}
		Collection<Map<String, String>> rolestrs = (Collection<Map<String, String>>) Jwts.parserBuilder()
				.setSigningKey(this.jwtTokenKey).build().parseClaimsJws(token).getBody().get(JwtUtils.TOKENAUTHKEY);
		return rolestrs.stream().map(str -> roles.stream()
				.filter(r -> r.name().equals(str.getOrDefault(JwtUtils.AUTHORITY, ""))).findFirst().orElse(Role.GUEST))
				.collect(Collectors.toList());
	}

	public String getUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(this.jwtTokenKey).build().parseClaimsJws(token).getBody()
				.getSubject();
	}

	public String getUuid(String token) {
		this.validateToken(token);
		return Jwts.parserBuilder().setSigningKey(this.jwtTokenKey).build().parseClaimsJws(token).getBody()
				.get(JwtUtils.UUID, String.class);
	}

	public String refreshToken(String token) {
		validateToken(token);
		Optional<Jws<Claims>> claimsOpt = this.getClaims(Optional.of(token));
		if (claimsOpt.isEmpty()) {
			throw new AuthenticationException("Invalid token claims");
		}
		Claims claims = claimsOpt.get().getBody();
		claims.setIssuedAt(new Date());
		claims.setExpiration(new Date(Instant.now().toEpochMilli() + validityInMilliseconds));
		String newToken = Jwts.builder().setClaims(claims).signWith(this.jwtTokenKey, SignatureAlgorithm.HS256)
				.compact();
		return newToken;
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}

	public Optional<String> resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith(JwtUtils.BEARER)) {
			return Optional.of(bearerToken.substring(7, bearerToken.length()));
		}
		return Optional.empty();
	}

	public boolean validateToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(this.jwtTokenKey).build().parseClaimsJws(token);
			String subject = Optional.ofNullable(claimsJws.getBody().getSubject())
					.orElseThrow(() -> new AuthenticationException("Invalid JWT token"));
			String uuid = Optional.ofNullable(claimsJws.getBody().get(JwtUtils.UUID, String.class))
					.orElseThrow(() -> new AuthenticationException("Invalid JWT token"));
			// LOG.info("Subject: {}, Uuid: {}, LoggedOutUsers: {}", subject, uuid,
			// JwtTokenService.loggedOutUsers.size());
			return this.loggedOutUsers.stream().noneMatch(
					myUserName -> subject.equalsIgnoreCase(myUserName.userName()) && uuid.equals(myUserName.uuid()));
		} catch (JwtException | IllegalArgumentException e) {
			throw new JwtTokenValidationException("Expired or invalid JWT token", e);
		}
	}

}
