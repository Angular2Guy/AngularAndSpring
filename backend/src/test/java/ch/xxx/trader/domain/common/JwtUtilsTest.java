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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {
    private static Key jwtKey;
    private static String validToken;
    private static String expiredToken;
    private static String tokenWithUserRole;
    private static String tokenWithGuestRole;
    private static String tokenWithBothRoles;

    @Mock
    private HttpServletRequest request;

    @BeforeAll
    static void setUp() {
        jwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        
        Date now = new Date();
        Date futureDate = new Date(now.getTime() + 3600000);
        Date pastDate = new Date(now.getTime() - 3600000);
        
        validToken = Jwts.builder()
                .setSubject("testuser")
                .claim(JwtUtils.TOKENAUTHKEY, "USER")
                .setExpiration(futureDate)
                .signWith(jwtKey)
                .compact();
        
        expiredToken = Jwts.builder()
                .setSubject("testuser")
                .claim(JwtUtils.TOKENAUTHKEY, "USER")
                .setExpiration(pastDate)
                .signWith(jwtKey)
                .compact();
        
        tokenWithUserRole = Jwts.builder()
                .setSubject("user")
                .claim(JwtUtils.TOKENAUTHKEY, Role.USERS.name())
                .setExpiration(futureDate)
                .signWith(jwtKey)
                .compact();
        
        tokenWithGuestRole = Jwts.builder()
                .setSubject("guest")
                .claim(JwtUtils.TOKENAUTHKEY, Role.GUEST.name())
                .setExpiration(futureDate)
                .signWith(jwtKey)
                .compact();
        
        tokenWithBothRoles = Jwts.builder()
                .setSubject("both")
                .claim(JwtUtils.TOKENAUTHKEY, Role.USERS.name() + "," + Role.GUEST.name())
                .setExpiration(futureDate)
                .signWith(jwtKey)
                .compact();
    }

    @Test
    void extractToken_fromHeaders_returnsToken() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + validToken);
        Optional<String> result = JwtUtils.extractToken(headers);
        assertTrue(result.isPresent());
        assertEquals(validToken, result.get());
    }

    @Test
    void extractToken_fromHeaders_returnsEmptyWhenNoAuthHeader() {
        Map<String, String> headers = new HashMap<>();
        Optional<String> result = JwtUtils.extractToken(headers);
        assertFalse(result.isPresent());
    }

    @Test
    void extractToken_fromHeaders_returnsEmptyWhenNullAuthHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, null);
        Optional<String> result = JwtUtils.extractToken(headers);
        assertFalse(result.isPresent());
    }

    @Test
    void extractToken_fromHeaders_returnsEmptyWhenNoBearerPrefix() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, "invalidtoken");
        Optional<String> result = JwtUtils.extractToken(headers);
        assertFalse(result.isPresent());
    }

    @Test
    void resolveToken_withBearerPrefix_returnsToken() {
        Optional<String> result = JwtUtils.resolveToken(JwtUtils.BEARER + validToken);
        assertTrue(result.isPresent());
        assertEquals(validToken, result.get());
    }

    @Test
    void resolveToken_withNullInput_returnsEmpty() {
        Optional<String> result = JwtUtils.resolveToken(null);
        assertFalse(result.isPresent());
    }

    @Test
    void resolveToken_withNoBearerPrefix_returnsEmpty() {
        Optional<String> result = JwtUtils.resolveToken("invalidtoken");
        assertFalse(result.isPresent());
    }

    @Test
    void getClaims_withValidToken_returnsClaims() {
        Optional<String> token = Optional.of(validToken);
        Optional<Jws<Claims>> result = JwtUtils.getClaims(token, jwtKey);
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getBody().getSubject());
    }

    @Test
    void getClaims_withEmptyOptional_returnsEmpty() {
        Optional<String> token = Optional.empty();
        Optional<Jws<Claims>> result = JwtUtils.getClaims(token, jwtKey);
        assertFalse(result.isPresent());
    }

    @Test
    void getClaims_withInvalidToken_throwsException() {
        Optional<String> token = Optional.of("invalidtoken");
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> 
            JwtUtils.getClaims(token, jwtKey));
    }

    @Test
    void getTokenRoles_withValidToken_returnsRole() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + validToken);
        String result = JwtUtils.getTokenRoles(headers, jwtKey);
        assertEquals("USER", result);
    }

    @Test
    void getTokenRoles_withExpiredToken_throwsExpiredJwtException() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + expiredToken);
        org.junit.jupiter.api.Assertions.assertThrows(ExpiredJwtException.class, () ->
            JwtUtils.getTokenRoles(headers, jwtKey));
    }

    @Test
    void getTokenRoles_withNoAuthHeader_returnsEmpty() {
        Map<String, String> headers = new HashMap<>();
        String result = JwtUtils.getTokenRoles(headers, jwtKey);
        assertEquals("", result);
    }

    @Test
    void getTokenUserRoles_withValidToken_returnsSubjectAndRole() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + validToken);
        JwtUtils.TokenSubjectRole result = JwtUtils.getTokenUserRoles(headers, jwtKey);
        assertNotNull(result);
        assertEquals("testuser", result.subject());
        assertEquals("USER", result.role());
    }

    @Test
    void getTokenUserRoles_withExpiredToken_throwsExpiredJwtException() {
        Map<String, String> headers = new HashMap<>();
        headers.put(JwtUtils.AUTHORIZATION, JwtUtils.BEARER + expiredToken);
        org.junit.jupiter.api.Assertions.assertThrows(ExpiredJwtException.class, () ->
            JwtUtils.getTokenUserRoles(headers, jwtKey));
    }

    @Test
    void getTokenUserRoles_withNoAuthHeader_returnsNullValues() {
        Map<String, String> headers = new HashMap<>();
        JwtUtils.TokenSubjectRole result = JwtUtils.getTokenUserRoles(headers, jwtKey);
        assertNotNull(result);
        assertEquals(null, result.subject());
        assertEquals(null, result.role());
    }

    @Test
    void checkToken_withUserRole_returnsTrue() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn(JwtUtils.BEARER + tokenWithUserRole);
        boolean result = JwtUtils.checkToken(request, jwtKey);
        assertTrue(result);
    }

    @Test
    void checkToken_withGuestRole_returnsFalse() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn(JwtUtils.BEARER + tokenWithGuestRole);
        boolean result = JwtUtils.checkToken(request, jwtKey);
        assertFalse(result);
    }

    @Test
    void checkToken_withBothRoles_returnsFalse() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn(JwtUtils.BEARER + tokenWithBothRoles);
        boolean result = JwtUtils.checkToken(request, jwtKey);
        assertFalse(result);
    }

    @Test
    void checkToken_withExpiredToken_throwsExpiredJwtException() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn(JwtUtils.BEARER + expiredToken);
        org.junit.jupiter.api.Assertions.assertThrows(ExpiredJwtException.class, () ->
            JwtUtils.checkToken(request, jwtKey));
    }

    @Test
    void checkToken_withNoAuthHeader_returnsFalse() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn(null);
        boolean result = JwtUtils.checkToken(request, jwtKey);
        assertFalse(result);
    }

    @Test
    void checkToken_withInvalidToken_returnsFalse() {
        when(request.getHeader(JwtUtils.AUTHORIZATION)).thenReturn("invalidtoken");
        boolean result = JwtUtils.checkToken(request, jwtKey);
        assertFalse(result);
    }
}
